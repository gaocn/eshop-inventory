package govind.inventory.controller;

import govind.inventory.dao.entity.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存的内存队列
 */
public class RebuildCacheQueue {
	private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<>(100);

	public void putProductInfo(ProductInfo info) {
		try {
			queue.put(info);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public ProductInfo takeProductInfo() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static RebuildCacheQueue getInstace() {
		return Singleton.instance;
	}
	private static class Singleton{
		private static RebuildCacheQueue instance;
		static {
			instance = new RebuildCacheQueue();
		}
	}
}
