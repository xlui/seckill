package app.xlui.seckill.service;

import app.xlui.seckill.entity.resp.Response;

public interface DistributedSeckillService {
	Response redisLockStart(long itemId, long userId);

	Response zkLockStart(long itemId, long userId);
}
