package com.path.variable.medidoc.fileprocessor.subscriber;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.path.variable.medidoc.fileprocessor.model.MedicalRecord;
import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.repository.PatientRecordRepository;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class FileUploadSubscriber {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MedicalRecordRepository medicalRecordRepository;

    private final PatientRecordRepository patientRecordRepository;


    public FileUploadSubscriber(MedicalRecordRepository medicalRecordRepository, PatientRecordRepository patientRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRecordRepository = patientRecordRepository;
    }

    public void readMessage(MqttMessage message) {
        FileUploadMessage fileUploadMessage;
        try {
            fileUploadMessage = OBJECT_MAPPER.readValue(message.getPayload(), FileUploadMessage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (fileUploadMessage != null) {
            processMessage(fileUploadMessage);
        }
    }

    private void processMessage(FileUploadMessage fileUploadMessage) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setExternalId(fileUploadMessage.id());
        medicalRecord.setExternalIdType(fileUploadMessage.idName());
        medicalRecord.setFileContents(fileUploadMessage.filePayload());
        medicalRecord.setFileFormat(fileUploadMessage.payloadFormat());
        MedicalRecord updatedMedicalRecord = medicalRecordRepository.save(medicalRecord);
        Optional<PatientRecord> patientRecord = patientRecordRepository.findByRecords_ExternalId(medicalRecord.getExternalId());
        patientRecord.ifPresent(pr -> {
            pr.getRecords().add(updatedMedicalRecord);
            patientRecordRepository.save(pr);
        });
    }
}
