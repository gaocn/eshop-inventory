package govind.inventory.product;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class CacheDataGeneraterConsumer implements Runnable{
	private KafkaConsumer<String, String> consumer;
	private String topic;

	public CacheDataGeneraterConsumer(String topic) {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "node128:9092,node129:9092");
		props.setProperty("group.id", "product-consumer");
		props.setProperty("key.deserializer", StringDeserializer.class.getName());
		props.setProperty("value.deserializer", StringDeserializer.class.getName());
		props.setProperty("auto.commit.enabled", "true");
		props.setProperty("auto.commit.interval.ms", "1000");
		this.topic = topic;
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(topic));
	}
	@Override
	public void run() {
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
			new Thread(new MessageProcessor(records.iterator())).start();
		}
	}
}
