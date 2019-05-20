package govind.inventory.ehcache;

import com.alibaba.fastjson.JSONObject;
import govind.inventory.dao.RedisDao;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.dao.entity.ShopInfo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("cacheService")
public class CacheServiceImpl implements ICacheService {
	//使用local缓存策略
	public static final String CACHE_NAME = "local";
	//全局缓存
	@Resource
	private RedisDao redisDao;

	/**
	 * 从本地缓存获取商品信息，没有取到返回null
	 */
	@Override
	@Cacheable(value = CACHE_NAME, key = "'product_' + #id")
	public ProductInfo getProductInfoFromLocalCache(Integer id) {
		return null;
	}

	@Override
	@Cacheable(value = CACHE_NAME, key = "'shop_' + #id")
	public ShopInfo getShopInfoFromLocalCache(Integer id) {
		return null;
	}

	/**
	 * 保存商品信息到Ehcache本地堆缓存
	 */
	@Override
	@CachePut(value = CACHE_NAME, key = "'product_' + #productInfo.getId()")
	public ProductInfo saveProductInfoToLocalCache(ProductInfo productInfo) {
		return productInfo;
	}

	/**
	 * 保存商品信息到redis缓存
	 */
	@Override
	public void saveProductInfoToRedisCache(ProductInfo productInfo) {
		String key = "product_" + productInfo.getId();
		redisDao.set(key, JSONObject.toJSONString(productInfo));
	}

	@Override
	public ProductInfo getProductInfoFromRedisCache(Integer productId) {
		String key = "product_" + productId;
		String json = redisDao.get(key);
		if (json != null) {
			return JSONObject.parseObject(json, ProductInfo.class);
		}
		return null;
	}

	/**
	 * 保存店铺信息到Ehcache本地堆缓存
	 */
	@Override
	@CachePut(value = CACHE_NAME, key = "'shop_' + #shopInfo.getId()")
	public ShopInfo saveShopInfoToLocalCache(ShopInfo shopInfo) {
		return shopInfo;
	}

	/**
	 * 保存店铺信息到redis缓存
	 */
	@Override
	public void saveShopInfoToRedisCache(ShopInfo shopInfo) {
		String key = "shop_" + shopInfo.getId();
		redisDao.set(key, JSONObject.toJSONString(shopInfo));
	}

	@Override
	public ShopInfo getShopInfoToRedisCache(Integer shopId) {
		String key = "shop_" + shopId;
		String json = redisDao.get(key);
		if (json != null) {
			return JSONObject.parseObject(json, ShopInfo.class);
		}
		return null;
	}
}
