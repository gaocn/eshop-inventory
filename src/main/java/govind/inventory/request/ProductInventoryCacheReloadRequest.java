package govind.inventory.request;

import govind.inventory.dao.entity.ProductInventory;
import govind.inventory.service.IProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 重新加载商品库存的缓存
 */
public class ProductInventoryCacheReloadRequest implements Request {
	private IProductInventoryService productInventoryService;
	private Integer productId;

	/**
	 * 是否强制刷新缓存
	 */
	private boolean forceRefresh;

	public ProductInventoryCacheReloadRequest(IProductInventoryService productInventoryService, Integer productId) {
		this(productInventoryService, productId, false);
	}

	public ProductInventoryCacheReloadRequest(IProductInventoryService productInventoryService, Integer productId, boolean forceRefresh) {
		this.productInventoryService = productInventoryService;
		this.productId = productId;
	}

	@Override
	public void process() {
		//从数据库中查询最新的商品库存数据量
		ProductInventory productInventory = productInventoryService.findProductInventory(productId);
		//将最新的商品库存数量刷新到redis缓存中
		productInventoryService.setProductInventoryCache(productInventory);
	}

	@Override
	public Integer getProductId() {
		return productId;
	}

	@Override
	public boolean isForceRefresh() {
		return forceRefresh;
	}
}
