package app.xlui.seckill.cloud.service.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "log")
public class Log implements Serializable {
	private static final long serialVerisonUID = 1L;
	@Id
	@GeneratedValue
	private int id;
	private int itemId;
	private int userId;
	private int remain;
	private Timestamp createTime;

	public Log() {
	}

	public Log(int itemId, int userId, int remain, Timestamp createTime) {
		this.itemId = itemId;
		this.userId = userId;
		this.remain = remain;
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getRemain() {
		return remain;
	}

	public void setRemain(int remain) {
		this.remain = remain;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
