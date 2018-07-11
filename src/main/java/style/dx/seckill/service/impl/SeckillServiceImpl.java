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

@Service
public class SeckillServiceImpl implements SeckillService {
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

	@Override
	@Transactional
	public Response normalStart(long itemId, long userId) {
		long count = itemRepository.findByItemId(itemId).getCount();
		if (count > 0) {
			itemRepository.seckill(itemId);
			SeckillSuccess succ = new SeckillSuccess(itemId, userId, (short) 0, new Timestamp(new Date().getTime()));
			seckillSuccessRepository.save(succ);
			return Response.ok(StateEnum.SUCCESS);
		}
		return Response.of(HttpStatus.ACCEPTED.value(), StateEnum.END);
	}

	@Override
	public Response lockStart(long itemId, long userId) {
		return null;
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
