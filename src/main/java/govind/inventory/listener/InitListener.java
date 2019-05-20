package govind.inventory.listener;

import govind.inventory.thread.RequestProcessorThreadPool;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化监听器，用于创建线程池和队列
 */
@Slf4j
public class InitListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("线程池和队列初始化");
		//初始化工作线程池和内存队列
		RequestProcessorThreadPool.init();
	}
}
