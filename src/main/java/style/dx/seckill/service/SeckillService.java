package style.dx.seckill.service;

import style.dx.seckill.entity.Item;
import style.dx.seckill.entity.resp.Response;

import java.util.List;

public interface SeckillService {
	List<Item> getItems();

	Item findById(long itemId);

	long successCount(long itemId);

	void reset(long itemId);

	Response normalStart(long itemId, long userId);

	Response lockStart(long itemId, long userId);

	Response aopLockStart(long itemId, long userId);

	Response dbPessimisticLockStart(long itemId, long userId);

	Response dbPessimisticLock2Start(long itemId, long userId);

	Response dbOptimisticLockStart(long itemId, long userId);
}
