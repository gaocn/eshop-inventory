package govind.inventory.ehcache;

import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.dao.entity.ShopInfo;

public interface ICacheService {
	ProductInfo getProductInfoFromLocalCache(Integer id);
	ShopInfo getShopInfoFromLocalCache(Integer id);

	/**
	 * 保存商品信息到Ehcache本地堆缓存中
	 */
	ProductInfo saveProductInfoToLocalCache(ProductInfo productInfo);
	/**
	 * 保存商品信息到Redis缓存中
	 */
	void saveProductInfoToRedisCache(ProductInfo productInfo);
	ProductInfo getProductInfoFromRedisCache(Integer productId);

	/**
	 * 保存商品店铺信息到Ehcache本地堆缓存中
	 */
	ShopInfo saveShopInfoToLocalCache(ShopInfo shopInfo);
	/**
	 * 保存商品店铺信息到Redis缓存中
	 */
	void saveShopInfoToRedisCache(ShopInfo shopInfo);
	ShopInfo getShopInfoToRedisCache(Integer shopId);

}
