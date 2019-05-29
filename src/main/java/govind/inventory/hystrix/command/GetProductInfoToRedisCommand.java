package govind.inventory.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.product.SpringContext;
import redis.clients.jedis.JedisCluster;

public class GetProductInfoToRedisCommand extends HystrixCommand<ProductInfo> {
	private Integer productId;
	public GetProductInfoToRedisCommand(Integer productId) {
		//所有redis访问采用同一个线程池
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup")));
		this.productId = productId;
	}
	@Override
	protected ProductInfo run() throws Exception {
		JedisCluster jedisCluster = (JedisCluster) SpringContext.getWebAppCtx().getBean("jedisClusterFactory");
		String key = "product_info_" + productId;
		String s = jedisCluster.get(key);
		return JSONObject.parseObject(s, ProductInfo.class);
	}

	@Override
	protected ProductInfo getFallback() {
		return null;
	}
}
