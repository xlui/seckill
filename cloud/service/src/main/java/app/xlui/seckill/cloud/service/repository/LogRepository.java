package app.xlui.seckill.cloud.service.repository;

import app.xlui.seckill.cloud.service.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Integer> {
	int countByItemId(int itemId);

	void deleteByItemId(int itemId);
}
