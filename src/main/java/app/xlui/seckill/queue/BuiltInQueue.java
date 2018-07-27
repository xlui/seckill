package app.xlui.seckill.queue;

import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.SeckillLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BuiltInQueue {
	private BlockingQueue<SeckillLog> blockingQueue;

	@Autowired
	private BuiltInQueue(SeckillProperties seckillProperties) {
		blockingQueue = new LinkedBlockingQueue<>(seckillProperties.getCount());
	}

	/**
	 * Add a seckill log to blocking queue
	 *
	 * @param seckillLog seckill log
	 * @return true if successfully add, false if full
	 * <p>
	 * add(e) true if queue is not full, IllegalStateException if queue is full
	 * put(e) If queue is not full, insert. If queue is full, wait.
	 * offer(e) true if queue is not full, false if full.
	 * offer(e, time, unit) true if queue is not full, false if full and have been waiting for some time
	 */
	public boolean produce(SeckillLog seckillLog) {
		return blockingQueue.offer(seckillLog);
	}

	/**
	 * Get a seckill log from blocking queue, if the queue is empty, wait.
	 *
	 * @return seckill log
	 * @throws InterruptedException interrupted while waiting
	 */
	public SeckillLog consume() throws InterruptedException {
		return blockingQueue.take();
	}

	/**
	 * Size of blocking queue
	 *
	 * @return size
	 */
	public int size() {
		return blockingQueue.size();
	}
}
