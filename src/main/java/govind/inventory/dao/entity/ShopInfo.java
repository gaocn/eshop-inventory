package govind.inventory.dao.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopInfo {
	private int id;
	private String name;
	private int level;
	private double goodCommentRate;
	private String modifiedTime;
}
