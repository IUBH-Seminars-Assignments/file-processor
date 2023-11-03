package com.path.variable.medidoc.fileprocessor.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.path.variable.medidoc.fileprocessor.dto.PatientRecordSearch;
import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import com.path.variable.medidoc.fileprocessor.repository.PatientRecordRepository;
import com.path.variable.medidoc.fileprocessor.subscriber.config.MqttTopics;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecordSearchSubscriber extends AbstractSubscriber<PatientRecordSearch> {

    private static final Logger LOG = LoggerFactory.getLogger(RecordSearchSubscriber.class);

    private final String searchTopic;

    private final String resultsTopic;

    private final PatientRecordRepository patientRecordRepository;

    public RecordSearchSubscriber(IMqttClient mqttClient, MqttTopics mqttTopics,
                                  PatientRecordRepository patientRecordRepository) {
        super(mqttClient);
        this.searchTopic = mqttTopics.getRecordSearchTopic();
        this.resultsTopic = mqttTopics.getRecordResultsTopic();
        this.patientRecordRepository = patientRecordRepository;
    }

    @Override
    public String getTopic() {
        return searchTopic;
    }

    @Override
    protected Class<PatientRecordSearch> getMessageClass() {
        return PatientRecordSearch.class;
    }

    @Override
    protected void processMessage(PatientRecordSearch message) {
        patientRecordRepository.findByRecords_ExternalId_AndRecords_ExternalIdType(message.externalId(),
                        message.externalIdType()).ifPresent(this::publish);
    }

    private void publish(PatientRecord patientRecord) {
        try {
            mqttClient.publish(resultsTopic, getJson(patientRecord).getBytes(), 1, false);
        } catch (MqttException e) {
            LOG.error("Could not publish message", e);
        }
    }

    private String getJson(PatientRecord record) {
        String json = "";
        try {
            json = OBJECT_MAPPER.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            LOG.error("Error while serializing message {}", record, e);
        }
        return json;
    }
}
