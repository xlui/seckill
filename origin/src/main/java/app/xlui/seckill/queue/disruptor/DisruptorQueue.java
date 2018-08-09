package app.xlui.seckill.queue.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executors;

public class DisruptorQueue {
	private static final int ringBufferSize = 1024;
	private static Disruptor<SeckillEvent> disruptor;

	static {
		SeckillEventFactory factory = new SeckillEventFactory();
		disruptor = new Disruptor<SeckillEvent>(factory, ringBufferSize, Executors.newCachedThreadPool());
		disruptor.handleEventsWith(new SeckillEventConsumer());
		disruptor.start();
	}

	public static void producer(SeckillEvent event) {
		RingBuffer<SeckillEvent> ringBuffer = disruptor.getRingBuffer();
		SeckillEventProducer seckillEventProducer = new SeckillEventProducer(ringBuffer);
		seckillEventProducer.seckill(event.getItemId(), event.getUserId());
	}
}
