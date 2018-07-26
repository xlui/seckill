package style.dx.seckill.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import style.dx.seckill.entity.SeckillSuccess;
import style.dx.seckill.entity.resp.Response;
import style.dx.seckill.entity.resp.StateEnum;
import style.dx.seckill.repository.ItemRepository;
import style.dx.seckill.repository.SeckillSuccessRepository;
import style.dx.seckill.service.DistributedSeckillService;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class DistributedSeckillServiceImpl implements DistributedSeckillService {
	private static final Logger logger = LoggerFactory.getLogger(DistributedSeckillServiceImpl.class);
	private final ItemRepository itemRepository;
	private final SeckillSuccessRepository seckillSuccessRepository;

	@Autowired
	public DistributedSeckillServiceImpl(ItemRepository itemRepository, SeckillSuccessRepository seckillSuccessRepository) {
		this.itemRepository = itemRepository;
		this.seckillSuccessRepository = seckillSuccessRepository;
	}

	private Response seckill(long itemId, long userId, long count) {
		if (count > 0) {
			itemRepository.seckill(itemId);
			SeckillSuccess succ = new SeckillSuccess(itemId, userId, count - 1, new Timestamp(new Date().getTime()));
			seckillSuccessRepository.save(succ);
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
		return seckill(itemId, userId, itemRepository.findCountByItemId(itemId));
	}

	@Override
	public Response zkLockStart(long itemId, long userId) {
		return null;
	}
}
