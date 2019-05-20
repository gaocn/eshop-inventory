package govind.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisConfig {
	@Bean
	public JedisCluster jedisClusterFactory() {
		Set<HostAndPort> hostAndPorts = new HashSet<>();
		hostAndPorts.add(new HostAndPort("node128", 7001));
		hostAndPorts.add(new HostAndPort("node128", 7002));
		hostAndPorts.add(new HostAndPort("node129", 7004));
		hostAndPorts.add(new HostAndPort("node129", 7007));
		return new JedisCluster(hostAndPorts);
	}
}
