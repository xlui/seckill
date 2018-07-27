package app.xlui.seckill.service.impl;

import app.xlui.seckill.distributed.redis.RedisLock;
import app.xlui.seckill.distributed.zookeeper.ZookeeperLock;
import app.xlui.seckill.entity.SeckillLog;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.entity.resp.StateEnum;
import app.xlui.seckill.repository.ItemRepository;
import app.xlui.seckill.repository.SeckillLogRepository;
import app.xlui.seckill.service.DistributedSeckillService;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class DistributedSeckillServiceImpl implements DistributedSeckillService {
	private static final Logger logger = LoggerFactory.getLogger(DistributedSeckillServiceImpl.class);
	private final ItemRepository itemRepository;
	private final SeckillLogRepository seckillLogRepository;

	@Autowired
	public DistributedSeckillServiceImpl(ItemRepository itemRepository, SeckillLogRepository seckillLogRepository) {
		this.itemRepository = itemRepository;
		this.seckillLogRepository = seckillLogRepository;
	}

	private Response seckill(long itemId, long userId, long count) {
		if (count > 0) {
			itemRepository.seckill(itemId);
			SeckillLog succ = new SeckillLog(itemId, userId, count - 1, new Timestamp(new Date().getTime()));
			seckillLogRepository.save(succ);
			return Response.ok(StateEnum.SUCCESS);
		} else {
			return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
		}
	}

	/**
	 * Also has the `over-sale` phenomenon, to solve, float up the lock
	 */
	@Override
	@Transactional
	public Response redisLockStart(long itemId, long userId) {
		RLock rLock = RedisLock.lock(String.valueOf(itemId), 5);
		try {
			return seckill(itemId, userId, itemRepository.findCountByItemId(itemId));
		} finally {
			RedisLock.unlock(rLock);
		}
	}

	@Override
	@Transactional
	public Response zkLockStart(long itemId, long userId) {
		boolean acquire = false;
		try {
			acquire = ZookeeperLock.acquire(3, TimeUnit.SECONDS);
			if (acquire) {
				return seckill(itemId, userId, itemRepository.findCountByItemId(itemId));
			} else {
				return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.MUCH);
			}
		} finally {
			if (acquire) {
				ZookeeperLock.release();
			}
		}
	}
}
