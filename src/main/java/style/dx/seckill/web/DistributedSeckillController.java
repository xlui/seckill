package style.dx.seckill.web;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import style.dx.seckill.config.SeckillProperties;
import style.dx.seckill.distributed.redis.RedisLock;
import style.dx.seckill.entity.resp.Response;
import style.dx.seckill.service.DistributedSeckillService;
import style.dx.seckill.service.SeckillService;

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

					RLock rLock = RedisLock.lock(String.valueOf(itemId), 5);
					Response response = distributedSeckillService.redisLockStart(itemId, user);
					log.info("user {}: {}", user, response.getMessage());
					RedisLock.unlock(rLock);
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
}
