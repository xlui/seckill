package style.dx.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

	public Response doSeckill(long itemId, long userId, long count) {
		if (count > 0) {
			itemRepository.seckill(itemId);
			SeckillSuccess succ = new SeckillSuccess(itemId, userId, count, new Timestamp(new Date().getTime()));
			seckillSuccessRepository.save(succ);
			return Response.ok(StateEnum.SUCCESS);
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}

	@Override
	@Transactional
	public Response normalStart(long itemId, long userId) {
		long count = itemRepository.findCountByItemId(itemId);
		return doSeckill(itemId, userId, count);
	}

	@Override
	@Transactional
	public Response lockStart(long itemId, long userId) {
		// if we add lock here, there may be one situation:
		// lock is released, while the transaction have not been committed
		// when other thread obtain the lock, and get the count from database,
		// it may see the dirty data, this is called <b>dirty-read.</b>
		// the solution is upper the lock, to the outside of the transaction.
		long count = itemRepository.findCountByItemId(itemId);
		return doSeckill(itemId, userId, count);
	}

	@Override
	public Response aopLockStart(long itemId, long userId) {
		return null;
	}

	@Override
	public Response dbPessimisticLockStart(long itemId, long userId) {
		return null;
	}

	@Override
	public Response dbPessimisticLock2Start(long itemId, long userId) {
		return null;
	}

	@Override
	public Response dbOptimisticLockStart(long itemId, long userId) {
		return null;
	}
}
