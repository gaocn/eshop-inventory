package govind.inventory.thread;

import govind.inventory.request.ProductInventoryCacheReloadRequest;
import govind.inventory.request.ProductInventoryDBDataUpdateRequest;
import govind.inventory.request.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程（后台线程）
 */
@Slf4j
public class RequestProcessorThread implements Callable<Boolean> {
	//自己监控的内存队列
	private ArrayBlockingQueue<Request> queue;
	public RequestProcessorThread(ArrayBlockingQueue<Request> queue) {
		this.queue = queue;
	}
	@Override
	public Boolean call() throws Exception {
		try {
			while (true) {
				//若队列满了或为空，则都会在执行操作时阻塞
				Request request = queue.take();
				boolean forceRefresh = request.isForceRefresh();
				if (!forceRefresh) {
					// 读请求去重
					Map<Integer, Boolean> flagMap = RequestQueue.getInstance().getFlagMap();
					if (request instanceof ProductInventoryDBDataUpdateRequest) {
						//若是一个更新数据库的请求，将productId对应的标识设置为true
						flagMap.put(request.getProductId(), true);
					} else if (request instanceof ProductInventoryCacheReloadRequest) {
						//若是一个缓存刷新的请求，若标识不为空且为true，则说明之前
						// 有一个这个商品的更新请求，将其设置为false
						Boolean flag = flagMap.get(request.getProductId());
						if (flag != null && flag) {
							flagMap.put(request.getProductId(), false);
						}
						// 如果是缓存刷新请求，而且标识不为空且为false，则说明前面
						// 已经有一个数据库更新请求和一个缓存刷新请求，此时什么都不用做
						if (flag != null && !flag) {
							//直接过滤掉
							return true;
						}
						//直接读缓存，若缓存没有则从数据库加载后刷新
						if (flag == null) {
							flagMap.put(request.getProductId(), false);
						}
					}
				}
				log.info("======工作线程处理商品id：{}======", request.getProductId());
				request.process();
			}
		} catch (Exception e) {
			log.error("任务处理线程{}出错：{}", Thread.currentThread().getName(), e.getMessage());
			return false;
		}
	}
}
