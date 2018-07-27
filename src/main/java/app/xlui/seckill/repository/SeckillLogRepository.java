package app.xlui.seckill.repository;

import app.xlui.seckill.entity.SeckillLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeckillLogRepository extends JpaRepository<SeckillLog, Long> {
	int countByItemId(long itemId);

	void deleteByItemId(long itemId);
}
