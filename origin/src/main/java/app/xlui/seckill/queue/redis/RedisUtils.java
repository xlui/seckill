package app.xlui.seckill.queue.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtils.class);
	private static final String KEY_PREFIX = "app:xlui:";
	@Resource
	private RedisTemplate<String, Serializable> redisTemplate;

	/**
	 * Set a <code>key-value</code> to redis
	 *
	 * @param key    key
	 * @param value  value
	 * @param expire the max available seconds of the key
	 * @return true if successfully set, false if not
	 */
	public boolean set(String key, Serializable value, long expire) {
		String realKey = KEY_PREFIX + key;
		try {
			redisTemplate.opsForValue().set(realKey, value);
			if (expire > 0) {
				redisTemplate.expire(realKey, expire, TimeUnit.SECONDS);
			}
			return true;
		} catch (Throwable throwable) {
			LOGGER.error("Failed to cache [key: " + realKey + ", value: " + value + "].");
			return false;
		}
	}

	/**
	 * Never expires
	 *
	 * @param key   key
	 * @param value value
	 * @return true if successfully set, false if not
	 */
	public boolean set(String key, Serializable value) {
		return set(key, value, -1);
	}

	/**
	 * get a value of <code>key</code> from redis
	 *
	 * @param key key
	 * @return value of key, or null
	 */
	public Serializable get(String key) {
		try {
			return redisTemplate.opsForValue().get(KEY_PREFIX + key);
		} catch (Throwable throwable) {
			LOGGER.error("Failed to get cache for key " + KEY_PREFIX + key);
			return null;
		}
	}

	/**
	 * remove a key-value pair from redis
	 *
	 * @param key key
	 * @return true if successfully remove, false if not
	 */
	public boolean remove(String key) {
		try {
			redisTemplate.delete(KEY_PREFIX + key);
			return true;
		} catch (Throwable throwable) {
			LOGGER.error("Failed to delete cache of key " + KEY_PREFIX + key);
			return false;
		}
	}
}
