package com.path.variable.medidoc.fileprocessor.subscriber;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.path.variable.medidoc.fileprocessor.model.MedicalRecord;
import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.repository.PatientRecordRepository;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

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
        patientRecordRepository.findByRecords_ExternalId(medicalRecord.getExternalId())
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
        patientRecord.getRecords().add(medicalRecord);
        patientRecordRepository.save(patientRecord);
    }
}
