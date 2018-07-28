package app.xlui.seckill.queue.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSender {
	private final StringRedisTemplate stringRedisTemplate;

	@Autowired
	public RedisSender(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	/**
	 * Send message to channel
	 *
	 * @param channel the channel of message listener
	 * @param message message to be sent
	 */
	public void send(String channel, String message) {
		stringRedisTemplate.convertAndSend(channel, message);
	}
}
