package com.path.variable.medidoc.fileprocessor.subscriber;


import com.path.variable.medidoc.fileprocessor.repository.MedicalRecordRepository;
import com.path.variable.medidoc.fileprocessor.repository.PatientRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUploadSubscriber {

    private final String topicName;

    private final MedicalRecordRepository medicalRecordRepository;

    private final PatientRecordRepository patientRecordRepository;


    public FileUploadSubscriber(@Value("${mqtt.topic}") String topicName, MedicalRecordRepository medicalRecordRepository, PatientRecordRepository patientRecordRepository) {
        this.topicName = topicName;
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRecordRepository = patientRecordRepository;
    }

    public void readMessage(String topic, byte[] contents) {

    }
}
