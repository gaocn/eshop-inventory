package govind.inventory.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.dao.entity.ShopInfo;
import govind.inventory.product.SpringContext;
import redis.clients.jedis.JedisCluster;

public class GetShopInfoToRedisCommand extends HystrixCommand<ShopInfo> {
	private Long shopId;
	public GetShopInfoToRedisCommand(Integer shopId) {
		//所有redis访问采用同一个线程池
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup")));
		this.shopId = shopId;
	}
	@Override
	protected ShopInfo run() throws Exception {
		JedisCluster jedisCluster = (JedisCluster) SpringContext.getWebAppCtx().getBean("jedisClusterFactory");
		String key = "shop_info_" + shopId;
		String s = jedisCluster.get(key);
		return JSONObject.parseObject(s, ShopInfo.class);
	}
}
