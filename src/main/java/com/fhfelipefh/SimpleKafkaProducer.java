package com.fhfelipefh;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleKafkaProducer extends SendMessage {

    private static final Logger logger = LoggerFactory.getLogger(SimpleKafkaProducer.class);

    private String BOOTSTRAP_SERVERS = "";
    private JSONObject jsonObject = new JSONObject();
    private Long numberOfMessages = 0L;
    private String key = "";
    private String kafkaTopic = "";
    private String kafkaMessage = "";
    private Properties properties = new Properties();

    public SimpleKafkaProducer(String bootstrapServers, String topic, String key, String message) {
        super();
        this.BOOTSTRAP_SERVERS = bootstrapServers;
        this.kafkaTopic = topic;
        this.key = key;
        this.kafkaMessage = message;
        setKafkaProperties();
    }

    @Override
    public void setKafkaProperties() {
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public void sendMessage() {
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, key, kafkaMessage);
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                logger.info("Message sent successfully. Metadata: Topic: " + metadata.topic() +
                        ", Partition: " + metadata.partition() +
                        ", Offset: " + metadata.offset() +
                        ", Timestamp: " + metadata.timestamp());
            } else {
                logger.error("Error sending message: ", exception);
            }
        });
        producer.close();
    }

    // Example usage
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "test-topic";
        String key = "test-key";
        String message = "Hello, Kafka!";

        SimpleKafkaProducer producer = new SimpleKafkaProducer(bootstrapServers, topic, key, message);
        producer.sendMessage();
    }
}
