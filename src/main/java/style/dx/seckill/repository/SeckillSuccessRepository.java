package style.dx.seckill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import style.dx.seckill.entity.SeckillSuccess;

public interface SeckillSuccessRepository extends JpaRepository<SeckillSuccess, Long> {
	int countByItemId(long itemId);

	void deleteByItemId(long itemId);
}
