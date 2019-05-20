package govind.inventory.service;

import govind.inventory.request.Request;
import govind.inventory.thread.RequestQueue;

/**
 * 请求异步执行服务
 */
public interface IRequestAsyncProcessorService {
	void process(Request request);

}
