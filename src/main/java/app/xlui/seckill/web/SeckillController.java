package app.xlui.seckill.web;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.service.SeckillService;
import app.xlui.seckill.web.util.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
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

	private String waitForResult(String type, long itemId, long start, CountDownLatch wait) {
		try {
			wait.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String ret;
		String cost = "cost about: " + (System.currentTimeMillis() - start) / 1000 + "s.";
		long count = seckillService.successCount(itemId);
		ret = "total seckill " + count + " items.";
		LOGGER.info("Seckill Type: [" + type + "].");
		LOGGER.info(ret);
		LOGGER.info(cost);
		return ret;
	}

	/**
	 * Version 1: no lock and no synchronization
	 */
	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response v1(long itemId) {
		return ControllerUtils.mock(
				"No Lock And No Synchronization",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, seckillService.normal(itemId, i).getMessage())
		);
	}

	/**
	 * Version 2: Reentrant Lock
	 */
	@RequestMapping(value = "/v2", method = RequestMethod.GET)
	public Response v2(long itemId) {
		return ControllerUtils.mock(
				"Reentrant Lock",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, seckillService.reentrantLock(itemId, i).getMessage())
		);
	}

	/**
	 * Version 3: AOP Reentrant Lock
	 */
	@RequestMapping(value = "/v3", method = RequestMethod.GET)
	public Response v3(long itemId) {
		return ControllerUtils.mock(
				"AOP Reentrant Lock",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, seckillService.aopLock(itemId, i).getMessage())
		);
	}

	@RequestMapping(value = "/v4", method = RequestMethod.GET)
	public Response v4(long itemId) {
		return ControllerUtils.mock(
				"Database Pessimistic Lock(SELECT ... FOR UPDATE)",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, seckillService.dbPessimisticLock(itemId, i).getMessage())
		);
	}

	@RequestMapping(value = "/v5", method = RequestMethod.GET)
	public Response v5(long itemId) {
		return ControllerUtils.mock(
				"Database Pessimistic Lock Version 2(Do seckill and check count at the same time)",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, seckillService.dbPessimisticLock2(itemId, i).getMessage())
		);
	}

	@RequestMapping(value = "/v6", method = RequestMethod.GET)
	public Response v6(long itemId) {
		return ControllerUtils.mock(
				"Database Optimistic Lock, using @Version",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, seckillService.dbOptimisticLock(itemId, i).getMessage())
		);
	}
}
