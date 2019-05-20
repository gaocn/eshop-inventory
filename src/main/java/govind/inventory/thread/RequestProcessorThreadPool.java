package govind.inventory.thread;

import govind.inventory.request.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求处理线程池
 * 通过静态内部类实现绝对线程安全的单例模式
 */
@Slf4j
public class RequestProcessorThreadPool {
	private static final int NUM = 10;
	//线程池，实际项目中设置多少个线程、每个线程监控的内存队列大小是多少都是放
	// 在外部配置文件中！
	private ExecutorService threadPool = Executors.newFixedThreadPool(NUM);

	private RequestProcessorThreadPool() {
		for (int i = 0; i < NUM; i++) {
			RequestQueue requestQueue = RequestQueue.getInstance();
			ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<>(100);
			//将线程与内存队列关系绑定，10个线程会立即将线程池填满
			requestQueue.addQueue(queue);
			threadPool.submit(new RequestProcessorThread(queue));
		}
	}
	public static RequestProcessorThreadPool getInstance() {
		return Singleton.getInstance();
	}

	public static void init() {
		getInstance();
	}
	/**
	 * 静态内部类初始化单例，在第一次调用Single通类时被初始化，并且JVM保证
	 * 只会被初始化一次，不管多少并发访问！
	 */
	private static class Singleton {
		private static RequestProcessorThreadPool instance;
		static {
			instance = new RequestProcessorThreadPool();
		}
		private static RequestProcessorThreadPool getInstance() {
			return instance;
		}
	}
}
