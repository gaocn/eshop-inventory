package govind.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求响应
 */
@Getter
@Setter
@AllArgsConstructor
public class Response {
	public static final String SUCCESS = "success";
	public static final String FAILURE = "failed";

	private String status;
	private String msg;
	private Object data;

	public static Response success() {
		return new Response("200", SUCCESS,"");
	}
	public static Response fail(String message) {
		return new Response("400", FAILURE, "");
	}
	public static Response success(Object data) {
		return new Response("200", SUCCESS,data);
	}
}
