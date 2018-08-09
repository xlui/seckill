package app.xlui.seckill.cloud.service.service;

import app.xlui.seckill.cloud.service.entity.Item;
import app.xlui.seckill.cloud.service.entity.Response;

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
	Item findById(int itemId);

	/**
	 * Count Items in table [logs] as successfully seckill user log
	 *
	 * @param itemId item id
	 * @return success count
	 */
	int successCount(int itemId);

	/**
	 * Reset item count and delete success log

	 * @param itemId item id
	 */
	void reset(int itemId);

	/**
	 * Normal version, without any synchronization or lock
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response normal(int itemId, int userId);

	/**
	 * Use synchronized to synchronization
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response syncLock(int itemId, int userId);

	/**
	 * Lock Version 1, use Reentrant Lock to synchronization, this implement will cause
	 * `dirty-read` phenomenon
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response reentrantLock(int itemId, int userId);

	/**
	 * Lock Version 2, AOP lock, will well performance
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response aopLock(int itemId, int userId);

	/**
	 * Database Pessimistic Lock Version 1, use `select ... for update` to lock result with select
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response dbPessimisticLock(int itemId, int userId);

	/**
	 * Database Pessimistic Lock Version 2, check available items before update
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response dbPessimisticLock2(int itemId, int userId);

	/**
	 * Database Optimistic Lock, use @Version to implement
	 *
	 * @param itemId item id
	 * @param userId user id
	 * @return response
	 */
	Response dbOptimisticLock(int itemId, int userId);
}
