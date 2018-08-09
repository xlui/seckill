package app.xlui.seckill.queue.disruptor;

import app.xlui.seckill.config.SpringUtil;
import app.xlui.seckill.service.SeckillService;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeckillEventConsumer implements EventHandler<SeckillEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SeckillEventConsumer.class);
	private SeckillService seckillService = SpringUtil.getBean(SeckillService.class);

	@Override
	public void onEvent(SeckillEvent event, long sequence, boolean endOfBatch) {
		LOGGER.info("user {}: {}",
				event.getUserId(),
				seckillService.normal(event.getItemId(), event.getUserId()).getMessage()
		);
	}
}
