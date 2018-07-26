package app.xlui.seckill.web.util;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.service.SeckillService;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class ControllerUtils {
	public static Response mock(String type, long itemId, Logger logger, ExecutorService executorService, SeckillService seckillService, SeckillProperties properties, Function<Long, Response> function) {
		seckillService.reset(itemId);
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());
		long start = System.currentTimeMillis();

		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executorService.execute(() -> {
				try {
					latch.await();

					Response response = function.apply(user);
					logger.info("user {}: {}", user, response.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

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
