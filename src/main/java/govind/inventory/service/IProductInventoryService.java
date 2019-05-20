package govind.inventory.service;

import govind.inventory.dao.entity.ProductInventory;

/**
 * 商品库存服务
 */
public interface IProductInventoryService {
	//更新商品库存
	void updateProductInventory(ProductInventory productInventory);
	//删除redis中商品库存的缓存
	void removeProductInventoryCache(ProductInventory productInventory);

	//根据商品id查找商品
	ProductInventory findProductInventory(Integer productId);
	//设置商品库存的缓存
	void setProductInventoryCache(ProductInventory productInventory);

	//获取产品库存的缓存
	ProductInventory getProductInventoryCache(Integer productId);

}
