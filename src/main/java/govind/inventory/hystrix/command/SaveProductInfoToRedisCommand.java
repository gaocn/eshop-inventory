package govind.inventory.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.product.SpringContext;
import redis.clients.jedis.JedisCluster;

public class SaveProductInfoToRedisCommand extends HystrixCommand<Boolean> {
	private ProductInfo productInfo;
	public SaveProductInfoToRedisCommand(ProductInfo productInfo) {
		//所有redis访问采用同一个线程池
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup")));
		this.productInfo = productInfo;
	}
	@Override
	protected Boolean run() throws Exception {
		JedisCluster jedisCluster = (JedisCluster) SpringContext.getWebAppCtx().getBean("jedisClusterFactory");
		String key = "product_info_" + productInfo.getId();
		jedisCluster.set(key, JSONObject.toJSONString(productInfo));
		return true;
	}

	@Override
	protected Boolean getFallback() {
		return true;
	}
}
