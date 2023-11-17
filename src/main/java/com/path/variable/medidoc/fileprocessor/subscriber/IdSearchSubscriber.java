package com.path.variable.medidoc.fileprocessor.subscriber;

import com.path.variable.medidoc.fileprocessor.dto.IdPair;
import com.path.variable.medidoc.fileprocessor.dto.IdSearch;
import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.subscriber.config.MqttTopics;
import org.eclipse.paho.mqttv5.client.IMqttClient;
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
    protected void processMessage(IdSearch idSearch, String clientId) {
        medicalRecordRepository.findAll(idSearch.toPageRequest()).stream()
                .map(IdPair::fromMedicalRecord).distinct()
                .forEach(pair -> publish(pair, clientId));
    }

    private void publish(IdPair idPair, String clientId) {
        publishResults(idPair, resultsTopic, clientId);
    }
}
