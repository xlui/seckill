package app.xlui.seckill.queue.redis;

import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisReceiver.class);
	private final SeckillService seckillService;
	private final RedisUtils redisUtils;

	@Autowired
	public RedisReceiver(SeckillService seckillService, RedisUtils redisUtils) {
		this.seckillService = seckillService;
		this.redisUtils = redisUtils;
	}

	/**
	 * Receive message from redis sender, this method will be invoked by redis message
	 * listener, with reflect.
	 *
	 * @param message <code>itemId,userId</code>
	 */
	public void receive(String message) {
		String[] ids = message.split(",");
		Response response = seckillService.dbPessimisticLock(Long.parseLong(ids[0]), Long.parseLong(ids[1]));
		LOGGER.info("user {}: {}", ids[1], response.getMessage());
	}
}
