package app.xlui.seckill.distributed.redis;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Config.class)
public class RedisAutoConfiguration {
	private final RedissonProperties redissonProperties;

	@Autowired
	public RedisAutoConfiguration(RedissonProperties redissonProperties) {
		this.redissonProperties = redissonProperties;
	}

	@Bean
	@ConditionalOnProperty(name = "seckill.redisson.addresses")
	public RedissonClient clusterRedissionClient() {
		Config config = new Config();
		config.useClusterServers()
				.setScanInterval(2_000)
				.addNodeAddress(redissonProperties.getAddresses());
		return Redisson.create(config);
	}

	/**
	 * Single Server Auto Create Bean
	 */
	@Bean
	@ConditionalOnProperty(name = "seckill.redisson.address")
	public RedissonClient singleRedissonClient() {
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
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public RedisLock redisLock(RedissonClient redissonClient) {
		RedisLock redisLock = new RedisLock();
		redisLock.setRedissonClient(redissonClient);
		return redisLock;
	}
}
