package app.xlui.seckill.web;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.service.DistributedSeckillService;
import app.xlui.seckill.service.SeckillService;
import app.xlui.seckill.web.util.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/d")
public class DistributedSeckillController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DistributedSeckillController.class);
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

	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response v1(long itemId) {
		return ControllerUtils.mock(
				"Redis Distributed Lock",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, distributedSeckillService.redisLock(itemId, i).getMessage())
		);
	}

	@RequestMapping(value = "/v2", method = RequestMethod.GET)
	public Response v2(long itemId) {
		return ControllerUtils.mock(
				"Zookeeper Distributed Lock",
				itemId,
				LOGGER,
				executor,
				seckillService,
				properties,
				(i) -> LOGGER.info("user {}: {}", i, distributedSeckillService.zkLock(itemId, i).getMessage())
		);
	}
}
