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

    private JList<String> messageList;
    private DefaultListModel<String> listModel;
    private JTextField textFieldBootstrapServers;
    private JButton sendButton;
    private JButton enableReceiveButton;
    private JLabel label;

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

        // Initialize GUI components
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Kafka Consumer GUI by fhfelipefh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(messageList);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
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
                    String message = "Key: " + record.key() + ", Value: " + record.value() +
                            ", Partition: " + record.partition() + ", Offset: " + record.offset();
                    logger.info("Received new record. " + message);
                    SwingUtilities.invokeLater(() -> listModel.addElement(message));
                }
            }
        } catch (Exception e) {
            logger.error("Error in Kafka consumer: ", e);
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        ReceiveMessage receiveMessage = new ReceiveMessage("localhost:9092", properties, "", "your-kafka-topic");
        new Thread(receiveMessage).start();
    }
}
