package com.path.variable.medidoc.fileprocessor;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static java.util.UUID.randomUUID;

@SpringBootApplication
public class FileProcessorApplication {

	@Bean
	@Scope("prototype")
	public IMqttClient mqttClient(@Value("${mqtt.address}") String address) throws MqttException {
		IMqttClient client =  new MqttClient(address, randomUUID().toString());
		client.connect();
		return client;
	}

	public static void main(String[] args) {
		SpringApplication.run(FileProcessorApplication.class, args);
	}

}
