package app.xlui.seckill.web;

import app.xlui.seckill.config.Const;
import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.SeckillLog;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.entity.resp.StateEnum;
import app.xlui.seckill.queue.builtin.BuiltInQueue;
import app.xlui.seckill.queue.disruptor.DisruptorQueue;
import app.xlui.seckill.queue.disruptor.SeckillEvent;
import app.xlui.seckill.queue.kafka.KafkaProducer;
import app.xlui.seckill.queue.redis.RedisSender;
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
@RequestMapping("/q")
public class QueueSeckillController {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueueSeckillController.class);
	private final SeckillService seckillService;
	private final SeckillProperties properties;
	private final BuiltInQueue builtInQueue;
	private final RedisSender redisSender;
	private final KafkaProducer kafkaProducer;
	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Autowired
	public QueueSeckillController(SeckillService seckillService, SeckillProperties properties, BuiltInQueue builtInQueue, RedisSender redisSender, KafkaProducer kafkaProducer) {
		this.seckillService = seckillService;
		this.properties = properties;
		this.builtInQueue = builtInQueue;
		this.redisSender = redisSender;
		this.kafkaProducer = kafkaProducer;
	}

	@RequestMapping(value = "/v1", method = RequestMethod.GET)
	public Response v1(long itemId) {
		return ControllerUtils.mock(
				"Built in Blocking Queue",
				itemId,
				LOGGER,
				executorService,
				seckillService,
				properties,
				(i) -> {
					SeckillLog seckillLog = new SeckillLog(itemId, i);
					boolean produce = builtInQueue.produce(seckillLog);
					if (!produce) {
						LOGGER.info("user {}: {}", i, StateEnum.MUCH);
					}
				}
		);
	}

	@RequestMapping(value = "/v2", method = RequestMethod.GET)
	public Response v2(long itemId) {
		int customers = properties.getCustomers();
		properties.setCustomers(customers / 3);
		Response resp = ControllerUtils.mock(
				"Disruptor Queue",
				itemId,
				LOGGER,
				executorService,
				seckillService,
				properties,
				(i) -> {
					SeckillEvent seckillEvent = new SeckillEvent(itemId, i);
					DisruptorQueue.producer(seckillEvent);
				}
		);
		properties.setCustomers(customers);
		return resp;
	}

	@RequestMapping(value = "/v3", method = RequestMethod.GET)
	public Response v3(long itemId) {
		return ControllerUtils.mock(
				"Redis Queue",
				itemId,
				LOGGER,
				executorService,
				seckillService,
				properties,
				(i) -> redisSender.send(Const.redisQueueChannel, itemId + "," + i)

		);
	}

	@RequestMapping(value = "/v4", method = RequestMethod.GET)
	public Response v4(long itemId) {
		return ControllerUtils.mock(
				"Kafka Queue",
				itemId,
				LOGGER,
				executorService,
				seckillService,
				properties,
				(i) -> kafkaProducer.send(Const.redisQueueChannel, itemId + "," + i)
		);
	}
}
