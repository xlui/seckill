package app.xlui.seckill.service;

import app.xlui.seckill.entity.resp.Response;

public interface DistributedSeckillService {
	/**
	 * Redis Distributed Lock
	 *
	 * @param itemId item to be seckilled
	 * @param userId user
	 * @return response
	 */
	Response redisLock(long itemId, long userId);

	/**
	 * Zookeeper Distributed Lock
	 *
	 * @param itemId item
	 * @param userId user
	 * @return response
	 */
	Response zkLock(long itemId, long userId);
}
