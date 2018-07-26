package style.dx.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import style.dx.seckill.aop.ServiceLock;
import style.dx.seckill.entity.Item;
import style.dx.seckill.entity.SeckillSuccess;
import style.dx.seckill.entity.resp.Response;
import style.dx.seckill.entity.resp.StateEnum;
import style.dx.seckill.repository.ItemRepository;
import style.dx.seckill.repository.SeckillSuccessRepository;
import style.dx.seckill.service.SeckillService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillServiceImpl implements SeckillService {
	// service is singleton by default, so in concurrent environment lock is also only one.
	private final Lock lock = new ReentrantLock(true);
	private final ItemRepository itemRepository;
	private final SeckillSuccessRepository seckillSuccessRepository;

	@Autowired
	public SeckillServiceImpl(ItemRepository itemRepository, SeckillSuccessRepository seckillSuccessRepository) {
		this.itemRepository = itemRepository;
		this.seckillSuccessRepository = seckillSuccessRepository;
	}

	@Override
	public List<Item> getItems() {
		return itemRepository.findAll();
	}

	@Override
	public Item findById(long itemId) {
		return itemRepository.findOne(itemId);
	}

	@Override
	public long successCount(long itemId) {
		return seckillSuccessRepository.countByItemId(itemId);
	}

	@Override
	@Transactional
	public void reset(long itemId) {
		seckillSuccessRepository.deleteByItemId(itemId);
		itemRepository.resetItemByItemId(itemId);
	}

	private Response doSeckill(long itemId, long userId, long count) {
		if (count > 0) {
			itemRepository.seckill(itemId);
			SeckillSuccess succ = new SeckillSuccess(itemId, userId, count - 1, new Timestamp(new Date().getTime()));
			seckillSuccessRepository.save(succ);
			return Response.ok(StateEnum.SUCCESS);
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}

	@Override
	@Transactional
	public Response normalStart(long itemId, long userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@Transactional
	public Response lockStart(long itemId, long userId) {
		// if we add `lock` here, there may be one situation:
		// lock is released, while the transaction have not been committed
		// at the same time another thread obtain the lock, and get the count
		// from database, it may see the dirty data, this is called <b>dirty-read.</b>
		// the solution is to `float up` the lock, to the outside of the transaction.
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@ServiceLock
	@Transactional
	public Response aopLockStart(long itemId, long userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	@Transactional
	public Response dbPessimisticLockStart(long itemId, long userId) {
		return doSeckill(itemId, userId, itemRepository.findCountByItemIdForUpdate(itemId));
	}

	@Override
	@Transactional
	public Response dbPessimisticLock2Start(long itemId, long userId) {
		int result = itemRepository.updateCountWhileUpperThan0(itemId);
		if (result > 0) {
			SeckillSuccess succ = new SeckillSuccess(itemId, userId, -1, new Timestamp(new Date().getTime()));
			seckillSuccessRepository.save(succ);
			return Response.ok(StateEnum.SUCCESS);
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}

	@Override
	@Transactional
	public Response dbOptimisticLockStart(long itemId, long userId) {
		Item item = itemRepository.findByItemId(itemId);
		if (item.getCount() > 0) {
			int result = itemRepository.seckillWithVersion(itemId, item.getVersion());
			if (result > 0) {
				SeckillSuccess succ = new SeckillSuccess(itemId, userId, -1, new Timestamp(new Date().getTime()));
				seckillSuccessRepository.save(succ);
				return Response.ok(StateEnum.SUCCESS);
			}
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}
}
