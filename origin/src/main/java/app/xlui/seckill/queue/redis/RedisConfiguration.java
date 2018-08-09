package app.xlui.seckill.queue.redis;

import app.xlui.seckill.config.Const;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfiguration {
	/**
	 * Initialize message listener
	 */
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(messageListenerAdapter, new PatternTopic(Const.redisQueueChannel));
		return container;
	}

	/**
	 * Create method invoker for message resolve via reflect
	 */
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisReceiver redisReceiver) {
		return new MessageListenerAdapter(redisReceiver, "receive");
	}

	/**
	 * Initialize string redis template
	 */
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}
}
