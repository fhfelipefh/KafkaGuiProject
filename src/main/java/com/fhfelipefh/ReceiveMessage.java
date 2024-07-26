package com.fhfelipefh;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Data
@NoArgsConstructor
public class ReceiveMessage extends JFrame implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveMessage.class);

    private String BOOTSTRAP_SERVERS = "";
    private JSONObject jsonObject = new JSONObject();
    private Long numberOfMessages = 0L;
    private String key = "";
    private String kafkaTopic = "";
    private String kafkaMessage = "";
    private Properties properties = new Properties();

    private JPanel panelInput = new JPanel();
    private JPanel panelOutput = new JPanel();
    private JTextField textFieldBootstrapServers;
    private JButton sendButton;
    private JButton enableReceiveButton;
    private JLabel label;
    private JFrame frame = new JFrame("Kafka Consumer GUI by fhfelipefh");

    public ReceiveMessage(String BOOTSTRAP_SERVERS, Properties properties, String key, String kafkaTopic) {
        this.BOOTSTRAP_SERVERS = BOOTSTRAP_SERVERS;
        this.properties = properties;
        this.key = key;
        this.kafkaTopic = kafkaTopic;

        // Set Kafka consumer properties
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-consumer-group");
    }

    @Override
    public void run() {
        logger.info("ReceiveMessage.run: Starting...");
        try {
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Collections.singletonList(kafkaTopic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Received new record. \n"
                            + "Key: " + record.key()
                            + "\n" + "Value: " + record.value()
                            + "\n" + "Partition: " + record.partition()
                            + "\n" + "Offset: " + record.offset());
                    JLabel status = new JLabel("Message received successfully! > " + numberOfMessages);
                    numberOfMessages++;
                    status.setForeground(Color.GREEN);
                    panelOutput.removeAll();
                    panelOutput.add(status);
                    panelOutput.setLayout(new FlowLayout(10, 10, 10));
                    frame.add(panelOutput);
                }
            }
        } catch (Exception e) {
            logger.error("Error in Kafka consumer: ", e);
        }
    }
}
