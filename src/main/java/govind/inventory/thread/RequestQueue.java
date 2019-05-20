package govind.inventory.thread;

import com.sun.org.apache.xpath.internal.operations.Bool;
import govind.inventory.request.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求内存队列
 */
public class RequestQueue {
	//内存队列
	private List<ArrayBlockingQueue<Request>> queues = new ArrayList<>();

	//标识，要求是多线程并发安全的Map
	private Map<Integer, Boolean> flagMap = new ConcurrentHashMap<>();

	public Map<Integer, Boolean> getFlagMap() {
		return flagMap;
	}

	public static RequestQueue getInstance() {
		return Singleton.getInstance();
	}

	//添加内存队列
	public void addQueue(ArrayBlockingQueue<Request> q) {
		queues.add(q);
	}
	//获取内存队列的数量
	public int queurSize() {
		return queues.size();
	}
	//获取内存队列
	public ArrayBlockingQueue<Request> getQueue(int idx) {
		return queues.get(idx);
	}

	private static class Singleton {
		private static RequestQueue instance;
		static {
			instance = new RequestQueue();
		}
		private static RequestQueue getInstance() {
			return instance;
		}
	}
}
