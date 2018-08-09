package app.xlui.seckill.cloud.service.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<Object, Object> map = new HashMap<>();

	public Response() {
	}

	public Response append(Object key, Object value) {
		map.put(key, value);
		return this;
	}

	public Object getKey(Object key) {
		return map.get(key);
	}

	public Map<Object, Object> getMap() {
		return map;
	}

	public void setMap(Map<Object, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		if (map.isEmpty()) {
			return "{}";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("{\n");
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				sb.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append(",\n");
			}
			sb.deleteCharAt(sb.length() - 2).append("}");
			return sb.toString();
		}
	}
}
