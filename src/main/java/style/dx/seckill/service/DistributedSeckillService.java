package style.dx.seckill.service;

import style.dx.seckill.entity.resp.Response;

public interface DistributedSeckillService {
	Response redisLockStart(long itemId, long userId);

	Response zkLockStart(long itemId, long userId);
}
