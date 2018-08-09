package app.xlui.seckill.service;

import app.xlui.seckill.entity.Item;
import app.xlui.seckill.entity.resp.Response;

import java.util.List;

public interface SeckillService {
	/**
	 * Get All Items
	 *
	 * @return item list
	 */
	List<Item> getItems();

	/**
	 * find item by id
	 *
	 * @param itemId item id
	 * @return item
	 */
	Item findById(long itemId);

	/**
	 * Count Items in table [logs] as successfully seckill user log
	 *
	 * @param itemId item id
	 * @return success count
	 */
	long successCount(long itemId);

	/**
	 * Reset item count and delete success log

	 * @param itemId item id
	 */
	void reset(long itemId);

	/**
	 * Normal version, without any synchronization or lock
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response normal(long itemId, long userId);

	/**
	 * Lock Version 1, use Reentrant Lock to synchronization, this implement will cause
	 * `dirty-read` phenomenon
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response reentrantLock(long itemId, long userId);

	/**
	 * Lock Version 2, AOP lock, will well performance
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response aopLock(long itemId, long userId);

	/**
	 * Database Pessimistic Lock Version 1, use `select ... for update` to lock result with select
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response dbPessimisticLock(long itemId, long userId);

	/**
	 * Database Pessimistic Lock Version 2, check available items before update
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response dbPessimisticLock2(long itemId, long userId);

	/**
	 * Database Optimistic Lock, use @Version to implement
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response dbOptimisticLock(long itemId, long userId);
}
