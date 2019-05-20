package govind.inventory.service;

import govind.inventory.dao.ProductInventoryDao;
import govind.inventory.dao.RedisDao;
import govind.inventory.dao.entity.ProductInventory;
import govind.inventory.thread.RequestQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品库存服务实现类
 */
@Service("productInvetoryService")
public class ProductInventoryServiceImpl implements IProductInventoryService {
	@Autowired
	private ProductInventoryDao productInventoryDao;
	@Autowired
	private RedisDao redisDao;

	@Override
	public void updateProductInventory(ProductInventory productInventory) {
		productInventoryDao.updateInventoryCnt(productInventory);
	}

	@Override
	public void removeProductInventoryCache(ProductInventory productInventory) {
		String key = "product:inventory:" + productInventory.getProductId();
		redisDao.delete(key);
	}

	@Override
	public ProductInventory findProductInventory(Integer productId) {
		ProductInventory inventory = productInventoryDao.findProductInventory(productId);
		return inventory;

	}
	@Override
	public void setProductInventoryCache(ProductInventory productInventory) {
		String key = "product:inventory:" + productInventory.getProductId();
		redisDao.set(key, productInventory.getInventoryCnt().toString());
	}

	@Override
	public ProductInventory getProductInventoryCache(Integer productId) {
		Long iventoryCnt = 0L;
		String key = "product:inventory:" + productId;
		String result = redisDao.get(key);
		if (result != null && !"".equals(result)) {
			try {
				iventoryCnt = Long.valueOf(result);
				return new ProductInventory(productId, iventoryCnt);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
