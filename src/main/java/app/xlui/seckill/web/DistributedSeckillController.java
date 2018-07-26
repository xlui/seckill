package app.xlui.seckill.web;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.service.DistributedSeckillService;
import app.xlui.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/d")
public class DistributedSeckillController {
	private static final Logger log = LoggerFactory.getLogger(DistributedSeckillController.class);
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private final DistributedSeckillService distributedSeckillService;
	private final SeckillService seckillService;
	private final SeckillProperties properties;

	@Autowired
	public DistributedSeckillController(SeckillService seckillService, SeckillProperties properties, DistributedSeckillService distributedSeckillService) {
		this.seckillService = seckillService;
		this.properties = properties;
		this.distributedSeckillService = distributedSeckillService;
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
		log.info("Seckill Type: [" + type + "].");
		log.info(ret);
		log.info(cost);
		return ret;
	}

	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response v1(long itemId) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				try {
					latch.await();

					Response response = distributedSeckillService.redisLockStart(itemId, user);
					log.info("user {}: {}", user, response.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return Response.ok(waitForResult("Redis Distributed Lock", itemId, start, wait));
	}

	@RequestMapping(value = "/v2", method = RequestMethod.GET)
	public Response v2(long itemId) {
		seckillService.reset(itemId);
//		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				try {
//					latch.await();

					Response response = distributedSeckillService.zkLockStart(itemId, user);
					log.info("user {}: {}", user, response.getMessage());
//				} catch (InterruptedException e) {
//					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
//			latch.countDown();
		}

		return Response.ok(waitForResult("Zookeeper Distributed Lock", itemId, start, wait));
	}


}
