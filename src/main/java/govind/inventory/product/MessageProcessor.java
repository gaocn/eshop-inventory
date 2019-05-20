package govind.inventory.product;

import com.alibaba.fastjson.JSONObject;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.dao.entity.ShopInfo;
import govind.inventory.ehcache.ICacheService;
import govind.inventory.lock.ZookeeperSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * 使用 kafka-console-producer.sh --broker-list node128:9092 --topic ShopProductLog生产数据进行测试：
 * 	{"productId":1, "serviceId":"productInfoService"}
 * 	{"shopId":1, "serviceId":"productShopService"}
 */
@Slf4j
public class MessageProcessor implements Runnable {
	private ICacheService cacheService;
	private Iterator<ConsumerRecord<String, String>>  iterator;
	public MessageProcessor(Iterator<ConsumerRecord<String, String>> iter) {
		this.iterator = iter;
		this.cacheService = (ICacheService) SpringContext.getWebAppCtx().getBean("cacheService");
	}
	@Override
	public void run() {
		while (iterator.hasNext()) {
			ConsumerRecord<String, String> record = iterator.next();
			log.info("消费消息：{}", record);

			//1. 将消息转换为JSON对象
			JSONObject jsonObject = JSONObject.parseObject(record.value());
			//2. 从json中提取出消息对应的服务标识
			String serviceId = jsonObject.getString("serviceId");

			if ("productInfoService".equals(serviceId)) {
				log.info("处理商品信息变更信息");
				processProductInfoChangeMessage(jsonObject);
			} else if ("productShopService".equals(serviceId)) {
				//处理商品店铺变更信息
				log.info("处理商品店铺变更信息");
				processShopInfoChangeMessage(jsonObject);
			}
		}
	}

	/**
	 * 处理商品信息变更的消息
	 */
	private void processProductInfoChangeMessage(JSONObject message) {
		//提取出商品id
		Integer productId = message.getInteger("productId");
		//调用提取出商品信息服务的接口，这里用模拟信息
		String productInfoJson = "{\"id\":2, \"name\":\"iphone8手机\", \"price\":6999,\"pictures\":\"a.jpg,b.jpg\",\"specification\":\"iphone8规格\",\"service\":\"售后服务\", \"color\":\"black\", \"size\":\"5.5\",\"shopId\":1,\"modifiedTime\":\"2019-05-17 20:00:00\"}";
		log.info("成功调用商品信息服务接口，返回内容：{}", productInfoJson);
		// 将查询的结果分别存放到Ehcache、Redis中
		ProductInfo productInfo = JSONObject.parseObject(productInfoJson, ProductInfo.class);
		cacheService.saveProductInfoToLocalCache(productInfo);
		log.info("=====获取保存包本地堆缓存中的商品信息：{}", cacheService.getProductInfoFromLocalCache(productId));

		/**
		 * 在将数据写入Redis之前，需要先获取ZK的分布式锁
		 */
		ZookeeperSession.getInstance().acquireLock(productId);
		ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productId);
		if (existedProductInfo != null) {
			// 比较当前数据时间版本与已有数据的时间版本相比较是新的就更新
			LocalDateTime existedTimestamp = LocalDateTime.parse(existedProductInfo.getModifiedTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime timestamp = LocalDateTime.parse(productInfo.getModifiedTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			if (existedTimestamp.isAfter(timestamp)) {
				log.info("要更新的数据版本较旧，跳过更新");
				return;
			}
		}
		cacheService.saveProductInfoToRedisCache(productInfo);
		ZookeeperSession.getInstance().releaseLock(productId);
	}

	/**
	 * 处理店铺信息
	 */
	private void processShopInfoChangeMessage(JSONObject message) {
		//提取出商品id
		Integer shopId = message.getInteger("shopId");
		//调用提取出商品信息服务的接口，这里用模拟信息
		String shopInfoJson = "{\"id\":2, \"name\":\"小王的手机店\", \"level\":5,\"goodCommentRate\":4.5, \"modifiedTime\":\"2019-05-17 20:00:00\"}";
		log.info("成功调用店铺信息服务接口，返回内容：{}", shopInfoJson);
		// 将查询的结果分别存放到Ehcache、Redis中
		ShopInfo shopInfo = JSONObject.parseObject(shopInfoJson, ShopInfo.class);
		cacheService.saveShopInfoToLocalCache(shopInfo);
		log.info("=====获取保存包本地堆缓存中的店铺信息：{}", cacheService.getShopInfoFromLocalCache(shopId));
		cacheService.saveShopInfoToRedisCache(shopInfo);
	}
}
