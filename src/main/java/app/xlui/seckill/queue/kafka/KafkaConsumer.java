package app.xlui.seckill.queue.kafka;

import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.queue.redis.RedisUtils;
import app.xlui.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
	private final SeckillService seckillService;
	private final RedisUtils redisUtils;

	@Autowired
	public KafkaConsumer(SeckillService seckillService, RedisUtils redisUtils) {
		this.seckillService = seckillService;
		this.redisUtils = redisUtils;
	}

	@KafkaListener(topics = {"seckill"})
	public void consume(String message) {
		String[] ids = message.split(",");
		Response response = seckillService.normal(Long.parseLong(ids[0]), Long.parseLong(ids[1]));
		LOGGER.info("user {}: {}", ids[1], response.getMessage());
	}
}
