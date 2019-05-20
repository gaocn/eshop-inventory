package govind.inventory.request;

import govind.inventory.dao.entity.ProductInventory;
import govind.inventory.service.IProductInventoryService;
import govind.inventory.service.ProductInventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 比如：商品发生了交易，那么就要修改该商品对应的库存。此时就会发送请求过来要求
 * 修改库存，这就是DataUpdateRequest。
 * 采用Cache Aside Pattern中的操作：
 * 1、删除缓存
 * 2、更新数据库
 * 注意：更新库存可能涉及几十个字段计算后才知道最终库存结果，这里就忽略了大量复
 * 杂的业务逻辑！
 */
public class ProductInventoryDBDataUpdateRequest implements Request {
	private ProductInventory productInventory;
	private IProductInventoryService productInventoryService;

	public ProductInventoryDBDataUpdateRequest(ProductInventory productInventory, IProductInventoryService productInventoryService) {
		this.productInventory = productInventory;
		this.productInventoryService = productInventoryService;
	}

	@Override
	public void process() {
		//删除redis中的缓存
		productInventoryService.removeProductInventoryCache(productInventory);
		//修改数据库中的库存
		productInventoryService.updateProductInventory(productInventory);
	}

	@Override
	public Integer getProductId() {
		return productInventory.getProductId();
	}

	@Override
	public boolean isForceRefresh() {
		return false;
	}
}
