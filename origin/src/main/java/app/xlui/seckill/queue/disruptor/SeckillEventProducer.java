package app.xlui.seckill.queue.disruptor;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;

public class SeckillEventProducer {
	private final RingBuffer<SeckillEvent> ringBuffer;
	private final static EventTranslatorVararg<SeckillEvent> eventTranslator = (event, sequence, args) -> {
		event.setItemId((long) args[0]);
		event.setUserId((long) args[1]);
	};

	public SeckillEventProducer(RingBuffer<SeckillEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void seckill(long itemId, long userId) {
		ringBuffer.publishEvent(eventTranslator, itemId, userId);
	}
}
