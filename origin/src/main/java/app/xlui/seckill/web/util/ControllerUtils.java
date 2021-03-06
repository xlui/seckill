package app.xlui.seckill.web.util;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.service.SeckillService;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Common Utility Class, includes the implement of high concurrency simulation
 */
public class ControllerUtils {
	public static Response mock(String type, long itemId, Logger logger, ExecutorService executorService, SeckillService seckillService, SeckillProperties properties, Consumer<Long> consumer) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());
		long start = System.currentTimeMillis();

		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executorService.execute(() -> {
				try {
					latch.await();

					consumer.accept(user);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return summary(type, itemId, start, wait, logger, seckillService);
	}

	public static Response summary(String type, long itemId, long start, CountDownLatch wait, Logger logger, SeckillService seckillService) {
		try {
			wait.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String cost = "Cost about: " + (System.currentTimeMillis() - start) / 1000 + "s.";
		long count = seckillService.successCount(itemId);
		logger.info("Seckill Type: [" + type + "].");
		logger.info("Total seckil " + count + " items.");
		logger.info(cost);
		return Response.ok("Seckill Type: [" + type + "]. Total seckill " + count + " items. " + cost);
	}
}
