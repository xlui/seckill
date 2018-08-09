package app.xlui.seckill.cloud.service.service.impl;

import app.xlui.seckill.cloud.service.config.SeckillProperties;
import app.xlui.seckill.cloud.service.distributed.redis.RedisLock;
import app.xlui.seckill.cloud.service.distributed.zookeeper.ZookeeperLock;
import app.xlui.seckill.cloud.service.entity.Log;
import app.xlui.seckill.cloud.service.entity.Response;
import app.xlui.seckill.cloud.service.repository.ItemRepository;
import app.xlui.seckill.cloud.service.repository.LogRepository;
import app.xlui.seckill.cloud.service.service.DistributedSeckillService;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static app.xlui.seckill.cloud.service.config.Const.*;

@Service
public class DistributedSeckillServiceImpl implements DistributedSeckillService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedSeckillServiceImpl.class);
    private final ItemRepository itemRepository;
    private final LogRepository logRepository;

    @Autowired
    public DistributedSeckillServiceImpl(ItemRepository itemRepository, LogRepository logRepository) {
        this.itemRepository = itemRepository;
        this.logRepository = logRepository;
    }

    private Response seckill(int itemId, int userId, int count) {
        if (count > 0) {
            itemRepository.seckill(itemId);
            Log succ = new Log(itemId, userId, count - 1, new Timestamp(new Date().getTime()));
            logRepository.save(succ);
            return SUCCESS;
        } else {
            return END;
        }
    }

    @Override
    @Transactional
    public Response redisLock(int itemId, int userId) {
        RLock lock = RedisLock.lock(String.valueOf(itemId), 10);
        try {
            return seckill(itemId, userId, itemRepository.findCountByItemId(itemId));
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Response zkLock(int itemId, int userId) {
        boolean acquire = false;
        try {
            acquire = ZookeeperLock.acquire(5, TimeUnit.SECONDS);
            if (acquire) {
                return seckill(itemId, userId, itemRepository.findCountByItemId(itemId));
            } else {
                return MUCH;
            }
        } finally {
            if (acquire) {
                ZookeeperLock.release();
            }
        }
    }
}
