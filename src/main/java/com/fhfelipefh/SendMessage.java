package com.fhfelipefh;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

@Data
@NoArgsConstructor
public class SendMessage extends JFrame implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SendMessage.class);
    private JsonConverter jsonConverter = new JsonConverter();

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
    private JLabel label;
    private JFrame frame = new JFrame("Kafka Producer GUI by fhfelipefh");

    private void sendMessage() {
        logger.info("SendMessage.sendMessage: Starting...");
        setKafkaProperties();
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> recordProducer = new ProducerRecord<>(kafkaTopic, key, kafkaMessage);
        logger.info("Key" + key);
        producer.send(recordProducer, (metadata, exception) -> {
            if (exception == null) {
                logger.info("Received new metadata. \n"
                        + "Topic: " + metadata.topic()
                        + "\n" + "Partition: " + metadata.partition()
                        + "\n" + "offset: " + metadata.offset()
                        + "\n" + "Timestamp: " + metadata.timestamp());
                logger.info("SendMessage.sendMessage.onCompletion: Message sent successfully!");
                JLabel status = new JLabel("Message sent successfully! > " + numberOfMessages);
                numberOfMessages++;
                status.setForeground(Color.GREEN);
                panelOutput.removeAll();
                panelOutput.add(status);
                panelOutput.setLayout(new FlowLayout(10, 10, 10));
                frame.add(panelOutput);
                frame.pack();
            } else {
                logger.info("Error " + exception.getMessage());
                JLabel status = new JLabel("Error " + exception.getMessage());
                status.setForeground(Color.RED);
                panelOutput.removeAll();
                panelOutput.add(status);
                panelOutput.setLayout(new FlowLayout(10, 10, 10));
                frame.add(panelOutput);
                frame.pack();
            }
        });
        producer.close();
    }

    private void setKafkaProperties() {
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    private void setFrameProperties() {
        frame.setSize(259, 469);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        logger.info("SendMessage.run: Starting...");
        logger.info("SendMessage.SendMessage: Starting...");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(2, 25));
        frame.add(panelInput);
        label = new JLabel("Bootstrap Servers: ");
        textFieldBootstrapServers = new JTextField(20);
        textFieldBootstrapServers.setText("localhost:9092");
        panelInput.add(label);
        panelInput.add(textFieldBootstrapServers);
        label = new JLabel("Topic: ");
        JTextField textFieldTopic = new JTextField(20);
        panelInput.add(label);
        panelInput.add(textFieldTopic);
        label = new JLabel("Key: ");
        JTextField textFieldKey = new JTextField(20);
        panelInput.add(label);
        panelInput.add(textFieldKey);
        label = new JLabel("Message: ");
        JTextField textFieldMessage = new JTextField(20);
        panelInput.add(label);
        panelInput.add(textFieldMessage);
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
                    kafkaTopic = textFieldTopic.getText();
                    key = textFieldKey.getText();
                    BOOTSTRAP_SERVERS = textFieldBootstrapServers.getText();
                    if (!kafkaTopic.isBlank() && !key.isBlank() && !BOOTSTRAP_SERVERS.isBlank()) {
                        jsonObject = new JSONObject();
                        kafkaMessage = String.valueOf(jsonConverter.convert(textFieldMessage.getText()));
                        if (kafkaMessage.length() > 2) {
                            logger.info("SendMessage.sendButton.actionPerformed: Sending message...");
                            sendMessage();
                        } else {
                            logger.info("SendMessage.sendButton.actionPerformed: Message is empty or blank");
                            JOptionPane.showMessageDialog(frame, "Message is empty or blank or invalid");
                        }
                    } else {
                        logger.info("SendMessage.sendButton.actionPerformed: Bootstrap Servers, Topic and Key are required");
                        JOptionPane.showMessageDialog(frame, "Bootstrap Servers, Topic and Key are required");
                    }
                }
        );
        panelInput.add(sendButton);
        frame.pack();
        setFrameProperties();
    }

}
