package govind.inventory.product;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Spring容器上下文
 */
public class SpringContext {
	private static WebApplicationContext webAppCtx;

	public static WebApplicationContext getWebAppCtx() {
		return webAppCtx;
	}

	public static void setWebAppCtx(WebApplicationContext webAppCtx) {
		SpringContext.webAppCtx = webAppCtx;
	}
}
