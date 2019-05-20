package govind.inventory.controller;

import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.ehcache.CacheServiceImpl;
import govind.inventory.ehcache.ICacheService;
import govind.inventory.lock.ZookeeperSession;
import govind.inventory.product.SpringContext;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 缓存重建线程
 */
@Slf4j
public class RebuidCacheThread implements Runnable {
	@Override
	public void run() {
		RebuildCacheQueue rebuildQueue = RebuildCacheQueue.getInstace();
		ZookeeperSession zkClient = ZookeeperSession.getInstance();
		CacheServiceImpl cacheService = (CacheServiceImpl) SpringContext.getWebAppCtx().getBean("cacheService");
		while (true) {
			ProductInfo productInfo = rebuildQueue.takeProductInfo();
			zkClient.acquireLock(productInfo.getId());

			/**
			 * 在将数据写入Redis之前，需要先获取ZK的分布式锁
			 */
			ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());
			if (existedProductInfo != null) {
				// 比较当前数据时间版本与已有数据的时间版本相比较是新的就更新
				LocalDateTime existedTimestamp = LocalDateTime.parse(existedProductInfo.getModifiedTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				LocalDateTime timestamp = LocalDateTime.parse(productInfo.getModifiedTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				if (existedTimestamp.isAfter(timestamp)) {
					log.info("要更新的数据版本较旧，跳过更新");
					continue;
				}
			}
			cacheService.saveProductInfoToLocalCache(productInfo);
			cacheService.saveProductInfoToRedisCache(productInfo);
			zkClient.releaseLock(productInfo.getId());
		}
	}
}
