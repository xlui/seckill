package style.dx.seckill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import style.dx.seckill.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@Modifying
	@Query("UPDATE Item i SET i.count=100 WHERE i.itemId=:itemId")
	void resetItemByItemId(@Param("itemId") long itemId);

	Item findByItemId(long itemId);

	@Modifying
	@Query("UPDATE Item i SET i.count=i.count-1 WHERE i.itemId=:itemId")
	void seckill(@Param("itemId") long itemId);
}
