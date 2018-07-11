package style.dx.seckill.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "seckill_success")
public class SeckillSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long id;
	@Column(name = "item_id", nullable = false)
	private long itemId;
	private long userId;
	private short state;
	private Timestamp createTime;

	public SeckillSuccess() {
	}

	public SeckillSuccess(long itemId, long userId, short state, Timestamp createTime) {
		this.itemId = itemId;
		this.userId = userId;
		this.state = state;
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
