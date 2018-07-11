package style.dx.seckill.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "item")
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "item_id", nullable = false)
	private long itemId;            // item id
	private String name;            // item name
	private int count;                // total items
	private Timestamp startTime;    // start time of seckill
	private Timestamp endTime;        // end time of seckill
	private Timestamp createTime;    // create time of the item
	@Version
	private int version;            // optimistic lock

	public Item() {
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
