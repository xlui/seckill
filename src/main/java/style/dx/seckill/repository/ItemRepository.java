package style.dx.seckill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import style.dx.seckill.entity.Item;

import javax.persistence.LockModeType;

public interface ItemRepository extends JpaRepository<Item, Long> {
	Item findByItemId(long itemId);

	@Query("SELECT i.count FROM Item i WHERE i.itemId=:id")
	long findCountByItemId(@Param("id") long itemId);

	// pessimistic write lock
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i.count FROM Item i WHERE i.itemId=:id")
	long findCountByItemIdForUpdate(@Param("id") long itemId);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1 WHERE i.itemId=:id AND i.count > 0")
	int updateCountWhileUpperThan0(@Param("id") long itemId);

	@Modifying
	@Query("UPDATE Item i SET i.count=300 WHERE i.itemId=:itemId")
	void resetItemByItemId(@Param("itemId") long itemId);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1 WHERE i.itemId=:itemId")
	void seckill(@Param("itemId") long itemId);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1, i.version=i.version+1 WHERE i.itemId=:id AND i.version=:v")
	int seckillWithVersion(@Param("id") long itemId, @Param("v") int version);
}
