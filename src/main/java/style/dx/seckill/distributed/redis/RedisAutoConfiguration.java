package style.dx.seckill.distributed.redis;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisAutoConfiguration {
	private final RedissonProperties redissonProperties;

	@Autowired
	public RedisAutoConfiguration(RedissonProperties redissonProperties) {
		this.redissonProperties = redissonProperties;
	}

	/**
	 * Single Server Auto Create Bean
	 */
	@Bean
	@ConditionalOnProperty(name = "redisson.address")
	public RedissonClient redissonClient() {
		Config config = new Config();
		SingleServerConfig singleServerConfig = config.useSingleServer()
				.setAddress(redissonProperties.getAddress())
				.setTimeout(redissonProperties.getTimeout())
				.setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
				.setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());
		if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
			singleServerConfig.setPassword("myredispassword");
		}
		return Redisson.create(config);
	}

	/**
	 * Configure redis lock instance
	 */
	@Bean
	public RedisLock redisLock(RedissonClient redissonClient) {
		RedisLock redisLock = new RedisLock();
		redisLock.setRedissonClient(redissonClient);
		return redisLock;
	}
}
