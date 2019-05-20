package govind.inventory.dao;

import govind.inventory.dao.entity.ProductInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductInventoryDao {
	void updateInventoryCnt(@Param("ic") ProductInventory ic);
	//根据商品id查询商品库存
	ProductInventory findProductInventory(@Param("productId") Integer productId);
}
