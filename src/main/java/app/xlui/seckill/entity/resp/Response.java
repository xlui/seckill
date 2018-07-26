package app.xlui.seckill.entity.resp;

public class Response {
	private static final long serialVersionUID = 1L;
	private int status;
	private String message;

	public Response() {
	}

	public Response(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public static Response of(int status, Object message) {
		return new Response(status, message.toString());
	}

	public static Response ok() {
		return Response.of(200, "success!");
	}

	public static Response ok(Object message) {
		return Response.of(200, message.toString());
	}

	public static Response error(int status, Object message) {
		return Response.of(status, message.toString());
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "{\n\t\"status\":" + status + ",\n\t\"message\":" + message + "\n}";
	}
}
