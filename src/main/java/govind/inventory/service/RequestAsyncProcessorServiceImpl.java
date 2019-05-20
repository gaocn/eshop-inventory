package govind.inventory.service;

import govind.inventory.request.ProductInventoryCacheReloadRequest;
import govind.inventory.request.ProductInventoryDBDataUpdateRequest;
import govind.inventory.request.Request;
import govind.inventory.thread.RequestQueue;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class RequestAsyncProcessorServiceImpl implements IRequestAsyncProcessorService {
	//内存队列
	RequestQueue requestQueue = RequestQueue.getInstance();

	/**
	 * 根据商品id对请求进行路由，路由到对应的内存队列中。
	 */
	@Override
	public void process(Request request) {
		try {
			ArrayBlockingQueue<Request> routingQueue = getRoutingQueue(request.getProductId());
			//将请求放入对应的队列中，完成路由操作
			routingQueue.put(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取路由到的内存队列
	 */
	private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId) {
		String key = String.valueOf(productId);
		int h;
		int hash = (productId == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
		//对hash值取模，得到对应的内存队列索引
		int idx = (requestQueue.queurSize() - 1) & hash;
		return requestQueue.getQueue(idx);
	}
}
