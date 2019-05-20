package govind.inventory.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 缓存配置管理类
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean manager = new EhCacheManagerFactoryBean();
		manager.setConfigLocation(new ClassPathResource("ehcache.xml"));
		manager.setShared(true);
		return manager;
	}
	@Bean
	public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean factoryBean) {
		return new EhCacheCacheManager(factoryBean.getObject());
	}
}
