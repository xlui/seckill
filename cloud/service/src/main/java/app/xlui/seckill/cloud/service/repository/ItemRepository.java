package app.xlui.seckill.cloud.service.repository;

import app.xlui.seckill.cloud.service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface ItemRepository extends JpaRepository<Item, Integer> {
	Item findById(int id);

	@Query("SELECT i.count FROM Item i WHERE i.id=:itemId")
	int findCountByItemId(@Param("itemId") int itemId);

	// pessimistic write lock
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i.count FROM Item i WHERE i.id=:itemId")
	int findCountByItemIdForUpdate(@Param("itemId") int id);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1 WHERE i.id=:itemId AND i.count > 0")
	int updateCountWhileUpperThan0(@Param("itemId") int id);

	@Modifying
	@Query("UPDATE Item i SET i.count=:#{@seckillProperties.count} WHERE i.id=:itemId")
	void resetItemByItemId(@Param("itemId") int id);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1 WHERE i.id=:itemId")
	void seckill(@Param("itemId") int itemId);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1, i.version=i.version+1 WHERE i.id=:itemId AND i.version=:v")
	int seckillWithVersion(@Param("itemId") int id, @Param("v") int version);
}
