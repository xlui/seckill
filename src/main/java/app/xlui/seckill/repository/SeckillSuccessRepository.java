package app.xlui.seckill.repository;

import app.xlui.seckill.entity.SeckillSuccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeckillSuccessRepository extends JpaRepository<SeckillSuccess, Long> {
	int countByItemId(long itemId);

	void deleteByItemId(long itemId);
}
