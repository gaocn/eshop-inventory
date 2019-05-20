package govind.inventory.dao.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 商品信息
 */
@Getter
@Setter
@ToString
public class ProductInfo{
	private int id;
	private String name;
	private String price;
	private String pictures;
	private String specification;
	private String service;
	private String color;
	private String size;
	private int shopId;
	private String modifiedTime;
}
