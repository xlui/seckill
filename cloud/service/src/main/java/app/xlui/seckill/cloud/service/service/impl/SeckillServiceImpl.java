package app.xlui.seckill.cloud.service.service.impl;

import app.xlui.seckill.cloud.service.aop.ServiceLock;
import app.xlui.seckill.cloud.service.entity.Item;
import app.xlui.seckill.cloud.service.entity.Log;
import app.xlui.seckill.cloud.service.entity.Response;
import app.xlui.seckill.cloud.service.repository.ItemRepository;
import app.xlui.seckill.cloud.service.repository.LogRepository;
import app.xlui.seckill.cloud.service.service.SeckillService;
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
	private static Response SUCCESS = new Response().append("status", "success").append("msg", "successfully seckill an item!!!");
	private static Response END = new Response().append("status", "end").append("msg", "seckill is end!");
	private final Lock lock = new ReentrantLock();
	private final ItemRepository itemRepository;
	private final LogRepository logRepository;

	@Autowired
	public SeckillServiceImpl(ItemRepository itemRepository, LogRepository logRepository) {
		this.itemRepository = itemRepository;
		this.logRepository = logRepository;
	}

	@Override
	public List<Item> getItems() {
		return itemRepository.findAll();
	}

	@Override
	public Item findById(int itemId) {
		return itemRepository.findById(itemId);
	}

	@Override
	public int successCount(int itemId) {
		return logRepository.countByItemId(itemId);
	}

	@Override
	@Transactional
	public void reset(int itemId) {
		logRepository.deleteByItemId(itemId);
		itemRepository.resetItemByItemId(itemId);
	}

	private Response doSeckill(int itemId, int userId, int count) {
		if (count > 0) {
			itemRepository.seckill(itemId);
			Log seckillLog = new Log(itemId, userId, count - 1, new Timestamp(new Date().getTime()));
			logRepository.save(seckillLog);
			return SUCCESS;
		}
		return END;
	}

	@Override
	@Transactional
	public Response normal(int itemId, int userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	public synchronized Response syncLock(int itemId, int userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@Transactional
	public Response reentrantLock(int itemId, int userId) {
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
	public Response aopLock(int itemId, int userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@Transactional
	public Response dbPessimisticLock(int itemId, int userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemIdForUpdate(itemId));
	}

	@Override
	@Transactional
	public Response dbPessimisticLock2(int itemId, int userId) {
		int result = itemRepository.updateCountWhileUpperThan0(itemId);
		if (result > 0) {
			Log seckillLog = new Log(itemId, userId, -1, new Timestamp(new Date().getTime()));
			logRepository.save(seckillLog);
			return SUCCESS;
		}
		return END;
	}

	@Override
	@Transactional
	public Response dbOptimisticLock(int itemId, int userId) {
		Item item = itemRepository.findById(itemId);
		if (item.getCount() > 0) {
			int result = itemRepository.seckillWithVersion(itemId, item.getVersion());
			if (result > 0) {
				Log seckillLog = new Log(itemId, userId, -1, new Timestamp(new Date().getTime()));
				logRepository.save(seckillLog);
				return SUCCESS;
			}
		}
		return END;
	}
}
