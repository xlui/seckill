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

	private String waitForResult(long itemId, long start, CountDownLatch wait) {
		try {
			wait.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String ret;
		String cost = "cost about: " + (System.currentTimeMillis() - start) / 1000 + "s.";
		long count = seckillService.successCount(itemId);
		ret = "total seckill " + count + " items.";
		LOGGER.info(ret);
		LOGGER.info(cost);
		return ret;
	}

	/**
	 * Version 1: no lock and no synchronization
	 */
	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response startV1(long itemId) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				try {
					latch.await();

					Response response = seckillService.normalStart(itemId, user);
					LOGGER.info("user {}: {}", user, response.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return Response.ok(waitForResult(itemId, start, wait));
	}

	/**
	 * Version 2: Reentrant Lock
	 */
	@RequestMapping(value = "/v2", method = RequestMethod.GET)
	public Response startV2(long itemId) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				try {
					latch.await();

					lock.lock();
					try {
						Response response = seckillService.lockStart(itemId, user);
						LOGGER.info("user {}: {}", user, response.getMessage());
					} finally {
						lock.unlock();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return Response.ok(waitForResult(itemId, start, wait));
	}

	/**
	 * Version 3: AOP Reentrant Lock
	 */
	@RequestMapping(value = "/v3", method = RequestMethod.GET)
	public Response startV3(long itemId) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				try {
					latch.await();

					Response response = seckillService.aopLockStart(itemId, user);
					LOGGER.info("user {}: {}", user, response.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return Response.ok(waitForResult(itemId, start, wait));
	}

	@RequestMapping(value = "/v4", method = RequestMethod.GET)
	public Response v4(long itemId) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executor.execute(() -> {
				try {
					latch.await();

					Response response = seckillService.dbPessimisticLockStart(itemId, user);
					LOGGER.info("user {}: {}", user, response.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return Response.ok(waitForResult(itemId, start, wait));
	}
}
