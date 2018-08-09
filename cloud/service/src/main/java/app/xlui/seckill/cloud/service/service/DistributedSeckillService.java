package app.xlui.seckill.cloud.service.service;

import app.xlui.seckill.cloud.service.entity.Response;

public interface DistributedSeckillService {
    /**
     * Redis Distributed Lock
     *
     * @param itemId item to be seckilled
     * @param userId user
     * @return response
     */
    Response redisLock(int itemId, int userId);

    /**
     * Zookeeper Distributed Lock
     *
     * @param itemId item
     * @param userId user
     * @return response
     */
    Response zkLock(int itemId, int userId);
}
