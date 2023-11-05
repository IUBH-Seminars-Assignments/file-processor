package com.path.variable.medidoc.fileprocessor.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.path.variable.medidoc.fileprocessor.dto.IdPair;
import com.path.variable.medidoc.fileprocessor.dto.IdSearch;
import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.subscriber.config.MqttTopics;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IdSearchSubscriber extends AbstractSubscriber<IdSearch> {

    private static final Logger LOG = LoggerFactory.getLogger(IdSearchSubscriber.class);

    private final String searchTopic;

    private final String resultsTopic;

    private final MedicalRecordRepository medicalRecordRepository;


    public IdSearchSubscriber(IMqttClient mqttClient, MqttTopics mqttTopics, MedicalRecordRepository medicalRecordRepository) {
        super(mqttClient);
        this.searchTopic = mqttTopics.getIdSearchTopic();
        this.resultsTopic = mqttTopics.getIdResultsTopic();
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public String getTopic() {
        return searchTopic;
    }

    @Override
    protected Class<IdSearch> getMessageClass() {
        return IdSearch.class;
    }

    @Override
    protected void processMessage(IdSearch idSearch) {
        medicalRecordRepository.findAll(idSearch.toPageRequest()).stream()
                .map(IdPair::fromMedicalRecord).distinct()
                .forEach(this::publishResults);
    }

    private void publishResults(IdPair pair) {
        String json = "";
        try {
            json = OBJECT_MAPPER.writeValueAsString(pair);
        } catch (JsonProcessingException e) {
            LOG.error("Error while serializing message {}", pair, e);
        }
        try {
            mqttClient.publish(resultsTopic, json.getBytes(), 1, false);
        } catch (MqttException e) {
            LOG.error("Could not publish message", e);
        }
    }
}
