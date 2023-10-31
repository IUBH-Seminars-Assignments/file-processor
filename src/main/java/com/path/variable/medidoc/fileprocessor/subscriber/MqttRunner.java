package com.path.variable.medidoc.fileprocessor.subscriber;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MqttRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MqttRunner.class);

    private final FileUploadSubscriber subscriber;

    private final IMqttClient mqttClient;

    private final String topic;

    public MqttRunner(@Value("${mqtt.address}") String address, @Value("${mqtt.topic}") String topic,
                      FileUploadSubscriber fileUploadSubscriber) {
        IMqttClient mqttClient = null;
        this.topic = topic;
        this.subscriber = fileUploadSubscriber;
        try {
            mqttClient = new MqttClient(address, UUID.randomUUID().toString());
            mqttClient.connect();
        } catch (MqttException e) {
            LOG.error("Error while receiving MQTT messages", e);
        }
        this.mqttClient = mqttClient;
    }

    @PostConstruct
    public void run() throws MqttException {
        mqttClient.setCallback(subscriber);
        mqttClient.subscribe(topic, 1);
    }
}
