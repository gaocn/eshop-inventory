package govind.inventory.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

@Component
public class RedisDao {
	@Autowired
	private JedisCluster jedisCluster;

	public void set(String key, String value) {
		jedisCluster.set(key, value);
	}

	public String get(String key) {
		return jedisCluster.get(key);
	}

	public void delete(String key) {
		jedisCluster.del(key);
	}
}
