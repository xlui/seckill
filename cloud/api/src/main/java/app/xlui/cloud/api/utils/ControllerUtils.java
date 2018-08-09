package app.xlui.cloud.api.utils;

import app.xlui.cloud.api.entity.Response;
import app.xlui.cloud.api.remote.CommonRemote;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Common Utility Class, includes the implement of high concurrency simulation
 */
public class ControllerUtils {
	public static Response mock(String type, int itemId, Logger logger, ExecutorService executorService, CommonRemote commonRemote, Consumer<Integer> consumer) {
		commonRemote.reset(itemId);
		CountDownLatch latch = new CountDownLatch(commonRemote.customers());
		CountDownLatch wait = new CountDownLatch(commonRemote.customers());
		long start = System.currentTimeMillis();

		for (int i = 1; i <= commonRemote.customers(); i++) {
			final int user = i;
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

		return summary(type, itemId, start, wait, logger, commonRemote);
	}

	public static Response summary(String type, int itemId, long start, CountDownLatch wait, Logger logger, CommonRemote commonRemote) {
		try {
			wait.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String cost = "Cost about: " + (System.currentTimeMillis() - start) / 1000 + "s.";
		int count = commonRemote.successCount(itemId);
		logger.info("Seckill Type: [" + type + "].");
		logger.info("Total seckil " + count + " items.");
		logger.info(cost);
		return new Response()
				.append("Seckill type", type)
				.append("Total cost", cost)
				.append("Total seckill", count);
	}
}
