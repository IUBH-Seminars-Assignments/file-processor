package com.path.variable.medidoc.fileprocessor.subscriber;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.path.variable.medidoc.fileprocessor.model.MedicalRecord;
import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.repository.PatientRecordRepository;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class FileUploadSubscriber implements MqttCallback {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadSubscriber.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MedicalRecordRepository medicalRecordRepository;

    private final PatientRecordRepository patientRecordRepository;


    public FileUploadSubscriber(MedicalRecordRepository medicalRecordRepository, PatientRecordRepository patientRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRecordRepository = patientRecordRepository;
    }

    @Override
    public void disconnected(MqttDisconnectResponse response) {
        LOG.info("Disconnected from {} because {}", response.getServerReference(), response.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        LOG.error("An error occurred while processing an mqtt message",exception);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        readMessage(message);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        LOG.info("Connection established with {}", serverURI);
    }

    private void readMessage(MqttMessage message) {
        FileUploadMessage fileUploadMessage = null;
        try {
            fileUploadMessage = OBJECT_MAPPER.readValue(message.getPayload(), FileUploadMessage.class);
        } catch (IOException e) {
            LOG.error("Error while reading message {}", message.getPayload(), e);
        }
        if (fileUploadMessage != null) {
            processMessage(fileUploadMessage);
        }
    }

    private void processMessage(FileUploadMessage fileUploadMessage) {
        MedicalRecord medicalRecord = createNewMedicalRecord(fileUploadMessage);
        mergeWithPatientRecord(medicalRecord);
    }

    private MedicalRecord createNewMedicalRecord(FileUploadMessage message) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setExternalId(message.id());
        medicalRecord.setExternalIdType(message.idName());
        medicalRecord.setFileContents(message.filePayload());
        medicalRecord.setFileFormat(message.payloadFormat());
        return medicalRecordRepository.save(medicalRecord);
    }

    private void mergeWithPatientRecord(MedicalRecord medicalRecord) {
        patientRecordRepository.findByRecords_ExternalId_AndRecords_ExternalIdType(medicalRecord.getExternalId(),
                        medicalRecord.getExternalIdType())
                .ifPresentOrElse(pr -> updatePatientRecord(pr, medicalRecord),
                        () -> createNewPatientRecord(medicalRecord));
    }

    private void updatePatientRecord(PatientRecord patientRecord, MedicalRecord medicalRecord) {
        patientRecord.getRecords().add(medicalRecord);
        patientRecordRepository.save(patientRecord);
    }

    private void createNewPatientRecord(MedicalRecord medicalRecord) {
        PatientRecord patientRecord = new PatientRecord();
        patientRecord.setRecords(new ArrayList<>());
        PatientRecord updated = patientRecordRepository.save(patientRecord);
        updated.getRecords().add(medicalRecord);
        patientRecordRepository.save(updated);

    }

    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
