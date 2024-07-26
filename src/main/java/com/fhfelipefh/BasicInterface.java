package com.fhfelipefh;

import lombok.Data;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@Data
public class BasicInterface extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(BasicInterface.class);
    private JsonConverter jsonConverter = new JsonConverter();
    private JSONObject jsonObject = new JSONObject();
    private JPanel panelInput = new JPanel();
    private JPanel panelOutput = new JPanel();
    private JButton buttonValidate = new JButton("Validate");
    private JFrame frame = new JFrame("Kafka GUI by fhfelipefh");

    public BasicInterface() {
        logger.info("BasicInterface.BasicInterface: Starting...");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(2, 25));
        frame.add(panelInput);
        frame.add(panelOutput);
        JLabel labelInput = new JLabel("Input JSON: ");
        JTextField textInput = new JTextField(20);
        panelInput.add(labelInput);
        panelInput.add(textInput);
        JLabel labelOutput = new JLabel("Output JSON: ");
        JTextField textOutput = new JTextField(20);
        panelOutput.add(labelOutput);
        panelOutput.add(textOutput);
        buttonValidate.addActionListener(e -> {
            if (textInput.getText().isBlank()) {
                JOptionPane.showMessageDialog(frame, "Input JSON is empty or blank");
            } else {
                jsonObject = jsonConverter.convert(textInput.getText());
                if (!jsonObject.isEmpty()) {
                    textOutput.setText(jsonObject.toString());
                } else {
                    textOutput.setText("");
                    JOptionPane.showMessageDialog(frame, "Error converting json");
                }
            }
        });
        panelInput.add(buttonValidate);
        panelInput.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelOutput.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.pack();
        setFrameProperties();
    }

    private void setFrameProperties() {
        frame.setSize(600, 130);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
