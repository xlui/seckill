package app.xlui.seckill.entity.resp;

public enum StateEnum {
	MUCH(2, "too many people, wait a moment"),
	SUCCESS(1, "successfully seckill!!!"),
	END(0, "seckill end!");

	private int state;
	private String msg;

	StateEnum(int state, String msg) {
		this.state = state;
		this.msg = msg;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}
}
