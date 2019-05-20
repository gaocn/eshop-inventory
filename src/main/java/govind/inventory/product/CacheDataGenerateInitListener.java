package govind.inventory.product;

import govind.inventory.lock.ZookeeperSession;
import govind.inventory.warn.CacheWarm;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 缓存数据生成服务初始化监听器
 */
@Slf4j
public class CacheDataGenerateInitListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//获取并设置Spring容器，在`MessageProcessor`中需要用到Spring容器就可以直接获取
		ServletContext ctx = sce.getServletContext();
		WebApplicationContext webAppCtx = WebApplicationContextUtils.getWebApplicationContext(ctx);
		SpringContext.setWebAppCtx(webAppCtx);
		//系统启动时开启Kafka消费线程
		new Thread(new CacheDataGeneraterConsumer("ShopProductLog")).start();
		ZookeeperSession.init();
		// 缓存预热程序
		//new CacheWarm().start();
		log.info("应用程序启动时，成功创建kafka消费者");
	}
}
