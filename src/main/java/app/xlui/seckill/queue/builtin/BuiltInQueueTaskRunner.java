package app.xlui.seckill.queue.builtin;

import app.xlui.seckill.entity.SeckillLog;
import app.xlui.seckill.queue.BuiltInQueue;
import app.xlui.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BuiltInQueueTaskRunner implements ApplicationRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BuiltInQueueTaskRunner.class);
	private final SeckillService seckillService;
	private final BuiltInQueue builtInQueue;

	@Autowired
	public BuiltInQueueTaskRunner(SeckillService seckillService, BuiltInQueue builtInQueue) {
		this.seckillService = seckillService;
		this.builtInQueue = builtInQueue;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//noinspection InfiniteLoopStatement
		while (true) {
			SeckillLog seckillLog = builtInQueue.consume();
			if (seckillLog != null) {
				LOGGER.info("user {}: {}",
						seckillLog.getUserId(),
						seckillService.normalStart(seckillLog.getItemId(), seckillLog.getUserId()).getMessage()
				);
			}
		}
	}
}
