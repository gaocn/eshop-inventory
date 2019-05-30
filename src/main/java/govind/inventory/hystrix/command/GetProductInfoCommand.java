package govind.inventory.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import govind.inventory.dao.entity.ProductInfo;

public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {
	private Integer productId;

	public GetProductInfoCommand(Integer productId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup")));
		this.productId = productId;
	}
	@Override
	protected ProductInfo run() throws Exception {
		//若查看结果为空，则直接返回空的数据
		if(productId == 100) {
			ProductInfo productInfo = new ProductInfo();
			productInfo.setId(productId);
			return productInfo;
		} else {
			String productInfoJson = "{\"id\":2, \"name\":\"iphone8手机\", \"price\":6999,\"pictures\":\"a.jpg,b.jpg\",\"specification\":\"iphone8规格\",\"service\":\"售后服务\", \"color\":\"black\", \"size\":\"5.5\",\"shopId\":1,\"modifiedTime\":\"2019-05-17 21:30:00\"}";
			// 将查询的结果分别存放到Ehcache、Redis中
			ProductInfo productInfo = JSONObject.parseObject(productInfoJson, ProductInfo.class);
			return productInfo;
		}
	}

	@Override
	protected ProductInfo getFallback() {
		//先走hbase冷备，若有数据可以直接返回，这里可以实现两级command
		// ....
		// 然后再走stubbed fallback
		ProductInfo productInfo = new ProductInfo();
		productInfo.setId(productId);
		return productInfo;
	}
}
