package com.path.variable.medidoc.fileprocessor.subscriber;


import com.path.variable.medidoc.fileprocessor.dto.FileUploadMessage;
import com.path.variable.medidoc.fileprocessor.model.MedicalRecord;
import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.repository.PatientRecordRepository;
import com.path.variable.medidoc.fileprocessor.subscriber.config.MqttTopics;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;

@Component
public class FileUploadSubscriber extends AbstractSubscriber<FileUploadMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadSubscriber.class);

    private final String topic;

    private final MedicalRecordRepository medicalRecordRepository;

    private final PatientRecordRepository patientRecordRepository;


    public FileUploadSubscriber(IMqttClient mqttClient, MqttTopics mqttTopics, MedicalRecordRepository medicalRecordRepository,
                                PatientRecordRepository patientRecordRepository) {
        super(mqttClient);
        this.topic = mqttTopics.getFileUploadTopic();
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRecordRepository = patientRecordRepository;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    protected Class<FileUploadMessage> getMessageClass() {
        return FileUploadMessage.class;
    }

    @Override
    protected void processMessage(FileUploadMessage fileUploadMessage) {
        MedicalRecord medicalRecord = createNewMedicalRecord(fileUploadMessage);
        mergeWithPatientRecord(medicalRecord);
    }

    private MedicalRecord createNewMedicalRecord(FileUploadMessage message) {
        LOG.info("Creating new medical record for {}", message.externalId());
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setExternalId(message.externalId());
        medicalRecord.setExternalIdType(message.externalIdType());
        medicalRecord.setFileContents(convertContents(message.fileContents()));
        medicalRecord.setFileFormat(message.fileFormat());
        return medicalRecordRepository.save(medicalRecord);
    }

    private String convertContents(String fileContents) {
        return new String(Base64.getDecoder().decode(fileContents));
    }

    private void mergeWithPatientRecord(MedicalRecord medicalRecord) {
        LOG.info("Merging medical record with patient record");
        patientRecordRepository.findByRecords_ExternalId_AndRecords_ExternalIdType(medicalRecord.getExternalId(),
                        medicalRecord.getExternalIdType())
                .ifPresentOrElse(pr -> updatePatientRecord(pr, medicalRecord),
                        () -> createNewPatientRecord(medicalRecord));
    }

    private void updatePatientRecord(PatientRecord patientRecord, MedicalRecord medicalRecord) {
        LOG.info("Updating patient record");
        patientRecord.getRecords().add(medicalRecord);
        patientRecordRepository.save(patientRecord);
    }

    private void createNewPatientRecord(MedicalRecord medicalRecord) {
        LOG.info("Creating new patient record");
        PatientRecord patientRecord = new PatientRecord();
        patientRecord.setRecords(new ArrayList<>());
        PatientRecord updated = patientRecordRepository.save(patientRecord);
        updated.getRecords().add(medicalRecord);
        patientRecordRepository.save(updated);

    }


}
