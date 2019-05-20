package govind.inventory.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductInventory {
	//商品id
	private Integer productId;
	//商品库存
	private Long inventoryCnt;
}
