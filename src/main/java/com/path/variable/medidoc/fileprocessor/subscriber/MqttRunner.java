package com.path.variable.medidoc.fileprocessor.subscriber;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MqttRunner {

    public MqttRunner(@Value("${mqtt.address}") String address, @Value("${mqtt.topic}") String topic,
                      FileUploadSubscriber fileUploadSubscriber) {
        try {
            IMqttClient mqttClient = new MqttClient(address, UUID.randomUUID().toString());
            mqttClient.connect();
            mqttClient.subscribe(topic, 2, (t, msg) -> fileUploadSubscriber.readMessage(msg));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }
}
