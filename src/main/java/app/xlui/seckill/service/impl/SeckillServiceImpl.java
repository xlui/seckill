package app.xlui.seckill.service.impl;

import app.xlui.seckill.aop.ServiceLock;
import app.xlui.seckill.entity.Item;
import app.xlui.seckill.entity.SeckillLog;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.entity.resp.StateEnum;
import app.xlui.seckill.repository.ItemRepository;
import app.xlui.seckill.repository.SeckillLogRepository;
import app.xlui.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillServiceImpl implements SeckillService {
	private final Lock lock = new ReentrantLock();
	private final ItemRepository itemRepository;
	private final SeckillLogRepository seckillLogRepository;

	@Autowired
	public SeckillServiceImpl(ItemRepository itemRepository, SeckillLogRepository seckillLogRepository) {
		this.itemRepository = itemRepository;
		this.seckillLogRepository = seckillLogRepository;
	}

	@Override
	public List<Item> getItems() {
		return itemRepository.findAll();
	}

	@Override
	public Item findById(long itemId) {
		return itemRepository.findByItemId(itemId);
	}

	@Override
	public long successCount(long itemId) {
		return seckillLogRepository.countByItemId(itemId);
	}

	@Override
	@Transactional
	public void reset(long itemId) {
		seckillLogRepository.deleteByItemId(itemId);
		itemRepository.resetItemByItemId(itemId);
	}

	private Response doSeckill(long itemId, long userId, long count) {
		if (count > 0) {
			itemRepository.seckill(itemId);
			SeckillLog seckillLog = new SeckillLog(itemId, userId, count - 1, new Timestamp(new Date().getTime()));
			seckillLogRepository.save(seckillLog);
			return Response.ok(StateEnum.SUCCESS);
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}

	@Override
	@Transactional
	public Response normal(long itemId, long userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@Transactional
	public Response reentrantLock(long itemId, long userId) {
		// if we add `lock` here, there may be one situation:
		// lock is released, while the transaction have not been committed
		// at the same time another thread obtain the lock, and get the count
		// from database, it may see the dirty data, this is called <b>dirty-read.</b>
		// the solution is to `float up` the lock, to the outside of the transaction.
		lock.lock();
		try {
			return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
		} finally {
			lock.unlock();
		}
	}

	@Override
	@ServiceLock
	@Transactional
	public Response aopLock(long itemId, long userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@Transactional
	public Response dbPessimisticLock(long itemId, long userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemIdForUpdate(itemId));
	}

	@Override
	@Transactional
	public Response dbPessimisticLock2(long itemId, long userId) {
		int result = itemRepository.updateCountWhileUpperThan0(itemId);
		if (result > 0) {
			SeckillLog seckillLog = new SeckillLog(itemId, userId, -1, new Timestamp(new Date().getTime()));
			seckillLogRepository.save(seckillLog);
			return Response.ok(StateEnum.SUCCESS);
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}

	@Override
	@Transactional
	public Response dbOptimisticLock(long itemId, long userId) {
		Item item = itemRepository.findByItemId(itemId);
		if (item.getCount() > 0) {
			int result = itemRepository.seckillWithVersion(itemId, item.getVersion());
			if (result > 0) {
				SeckillLog seckillLog = new SeckillLog(itemId, userId, -1, new Timestamp(new Date().getTime()));
				seckillLogRepository.save(seckillLog);
				return Response.ok(StateEnum.SUCCESS);
			}
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}
}
