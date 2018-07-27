package app.xlui.seckill.web;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.SeckillLog;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.entity.resp.StateEnum;
import app.xlui.seckill.queue.BuiltInQueue;
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
@RequestMapping("/q")
public class QueueSeckillController {
	private static final Logger logger = LoggerFactory.getLogger(QueueSeckillController.class);
	private final SeckillService seckillService;
	private final SeckillProperties properties;
	private final BuiltInQueue builtInQueue;
	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Autowired
	public QueueSeckillController(SeckillService seckillService, SeckillProperties properties, BuiltInQueue builtInQueue) {
		this.seckillService = seckillService;
		this.properties = properties;
		this.builtInQueue = builtInQueue;
	}

	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response v1(long itemId) {
		seckillService.reset(itemId);
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CountDownLatch latch = new CountDownLatch(properties.getCustomers());
		CountDownLatch wait = new CountDownLatch(properties.getCustomers());
		long start = System.currentTimeMillis();

		for (int i = 1; i <= properties.getCustomers(); i++) {
			final long user = i;
			executorService.execute(() -> {
				try {
					latch.await();

					SeckillLog seckillLog = new SeckillLog(itemId, user);
					boolean produce = builtInQueue.produce(seckillLog);
					if (!produce) {
						logger.info("user {}: {}", user, StateEnum.MUCH);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					wait.countDown();
				}
			});
			latch.countDown();
		}

		return Response.ok("Seckill Type: [Built in Blocking Queue]. Total cost " + (System.currentTimeMillis() - start) / 1000 + "s.");
	}
}
