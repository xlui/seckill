package app.xlui.seckill.queue.disruptor;

import java.io.Serializable;

public class SeckillEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private long itemId;
	private long userId;

	public SeckillEvent() {
	}

	public SeckillEvent(long itemId, long userId) {
		this.itemId = itemId;
		this.userId = userId;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
