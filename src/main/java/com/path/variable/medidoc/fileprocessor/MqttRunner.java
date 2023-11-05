package com.path.variable.medidoc.fileprocessor;

import com.path.variable.medidoc.fileprocessor.subscriber.AbstractSubscriber;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MqttRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MqttRunner.class);

    private final List<AbstractSubscriber<?>> subscribers;

    private final IMqttClient mqttClient;


    public MqttRunner(IMqttClient mqttClient, List<AbstractSubscriber<?>> subscribers) {
        this.subscribers = subscribers;
        this.mqttClient = mqttClient;
    }

    @PostConstruct
    public void run() throws MqttException {
        for (AbstractSubscriber<?> subscriber : subscribers) {
            mqttClient.subscribe(subscriber.getTopic(), 1, subscriber::messageArrived);
            LOG.info("Subscribed to topic {}", subscriber.getTopic());
        }
    }
}
