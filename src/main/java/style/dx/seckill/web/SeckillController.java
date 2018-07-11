package style.dx.seckill.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import style.dx.seckill.config.Const;
import style.dx.seckill.entity.resp.Response;
import style.dx.seckill.service.SeckillService;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckill")
public class SeckillController {
	private static final int CUSTOMERS = 1000;
	private static final Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
			Const.processors,
			Const.processors * 2,
			0,
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(1000),
			Executors.defaultThreadFactory(),
			new ThreadPoolExecutor.AbortPolicy()
	);
	private final SeckillService seckillService;

	@Autowired
	public SeckillController(SeckillService seckillService) {
		this.seckillService = seckillService;
	}

	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response startV1(long itemId) {
		seckillService.reset(itemId);
		final long id = itemId;
		LOGGER.info("start seckill version 1, current time [" + new Date() + "]");
		for (int i = 1; i <= CUSTOMERS; i++) {
			final long user = i;
			executor.execute(() -> {
				Response response = seckillService.normalStart(id, user);
				if (response != null) {
					LOGGER.info("user {}: {}", user, response.getMessage());
				} else {
					LOGGER.info("user {}: {}", user, "Tooooo much people, please wait a while!");
				}
			});
		}
		try {
			Thread.sleep(1000 * 10);
			long count = seckillService.successCount(itemId);
			LOGGER.info("total seckill {} items.", count);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Response.ok();
	}
}
