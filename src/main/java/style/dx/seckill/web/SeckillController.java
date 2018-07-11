package style.dx.seckill.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import style.dx.seckill.config.SeckillProperties;
import style.dx.seckill.entity.resp.Response;
import style.dx.seckill.service.SeckillService;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
//@RequestMapping("/seckill")
public class SeckillController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);
	private static final Lock lock = new ReentrantLock(true);
	//	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
			0,
			Integer.MAX_VALUE,
			60L,
			TimeUnit.SECONDS,
			new SynchronousQueue<>(),
			Executors.defaultThreadFactory(),
			new ThreadPoolExecutor.AbortPolicy()
	);
	private final SeckillService seckillService;
	private final SeckillProperties properties;

	@Autowired
	public SeckillController(SeckillService seckillService, SeckillProperties properties) {
		this.seckillService = seckillService;
		this.properties = properties;
	}

	private String waitForResult(long itemId) {
		String ret = "";
		try {
			Thread.sleep(1000 * properties.getWaittime());
			long count = seckillService.successCount(itemId);
			ret = "total seckill " + count + " items.";
			LOGGER.info(ret);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response startV1(long itemId) {
		seckillService.reset(itemId);

		LOGGER.info("start seckill version 1, current time [" + new Date() + "]");
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				Response response = seckillService.normalStart(itemId, user);
				LOGGER.info("user {}: {}", user, response.getMessage());
			});
		}

		return Response.ok(waitForResult(itemId));
	}

	@RequestMapping(value = "/v2", method = RequestMethod.GET)
	public Response startV2(long itemId) {
		seckillService.reset(itemId);

		LOGGER.info("start seckill version 2, current time: " + new Date());
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				lock.lock();
				try {
					Response response = seckillService.lockStart(itemId, user);
					LOGGER.info("user {}: {}", user, response.getMessage());
				} finally {
					lock.unlock();
				}
			});
		}

		return Response.ok(waitForResult(itemId));
	}
}
