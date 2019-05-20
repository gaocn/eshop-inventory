package govind.inventory;

import govind.inventory.product.CacheDataGenerateInitListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 库存服务
 */
@SpringBootApplication
@MapperScan(basePackages = "govind.inventory.dao")
public class EshopInventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(EshopInventoryApplication.class, args);
	}

	//Java web应用做系统的初始化，一般会在ServletContextListener中做，
	// 该Listener会跟随整个web应用启动，因此线程池和内存队列的初始化就这里面实现。
	@Bean
	public ServletListenerRegistrationBean servletListenerRegistrationBean() {
		ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
		//bean.setListener(new InitListener());
		bean.setListener(new CacheDataGenerateInitListener());
		return bean;
	}

}
