package govind.inventory.controller;

import com.alibaba.fastjson.JSONObject;
import govind.inventory.dao.RedisDao;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.ehcache.ICacheService;
import govind.inventory.hystrix.command.GetProductInfoCommand;
import govind.inventory.warn.CacheWarm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class RedisController {
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private RedisDao redisDao;

	@GetMapping("/redis/{key}/{value}")
	@ResponseBody
	public Object set(@PathVariable("key") String key, @PathVariable("value")String value) {
		redisDao.set(key, value);
		return "ok";
	}

	@GetMapping("/redis/{key}")
	@ResponseBody
	public Object get(@PathVariable("key") String key) {
		return redisDao.get(key);
	}

	@GetMapping("/getProductInfo/{id:\\d+}")
	@ResponseBody
	public String getProductInfo(@PathVariable("id") String key) {
		String cacheKey = "product_" + key;
		String productInfo = redisDao.get(cacheKey);

		//从本地缓存中获取
		if (productInfo == null) {
			ProductInfo pi = cacheService.getProductInfoFromLocalCache(Integer.valueOf(key));
			if (pi != null) {
				productInfo = JSONObject.toJSONString(pi);
			}
		}

		if (productInfo == null) {
			GetProductInfoCommand command = new GetProductInfoCommand(Long.valueOf(key));
			ProductInfo productInfo1 = command.execute();
			log.info("成功从商品服务拉取数据：{}", productInfo1);
			//将数据推送到一个内存队列中
			RebuildCacheQueue.getInstace().putProductInfo(productInfo1);
		}
		return productInfo;
	}

	@GetMapping("/getShopInfo/{id:\\d+}")
	@ResponseBody
	public String getShopInfo(@PathVariable("id") String key) {
		String cacheKey = "shop_" + key;
		String shopInfo = redisDao.get(cacheKey);

		if (shopInfo == null) {
			//TODO 需要从数据源拉取数据，重建缓存！
		}
		return shopInfo;
	}

	@GetMapping("/cacheWarm")
	@ResponseBody
	public Object cacheWarm() {
		Map map = new HashMap<String, String>();
		//每次调用就其中一个线程进行缓存预热
		new CacheWarm().start();
		map.put("info", "成功启动缓存预热程序");
		return map;
	}
}
