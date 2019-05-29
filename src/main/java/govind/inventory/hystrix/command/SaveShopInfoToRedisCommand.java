package govind.inventory.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import govind.inventory.dao.entity.ProductInfo;
import govind.inventory.dao.entity.ShopInfo;
import govind.inventory.product.SpringContext;
import redis.clients.jedis.JedisCluster;

public class SaveShopInfoToRedisCommand extends HystrixCommand<Boolean> {
	private ShopInfo shopInfo;
	public SaveShopInfoToRedisCommand(ShopInfo shopInfo) {
		//所有redis访问采用同一个线程池
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup")));
		this.shopInfo = shopInfo;
	}
	@Override
	protected Boolean run() throws Exception {
		JedisCluster jedisCluster = (JedisCluster) SpringContext.getWebAppCtx().getBean("jedisClusterFactory");
		String key = "shop_info_" + shopInfo.getId();
		jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
		return true;
	}
}
