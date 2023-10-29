package com.path.variable.medidoc.fileprocessor.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "patient_record")
public class PatientRecord {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MedicalRecord> records;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MedicalRecord> getRecords() {
        return records;
    }

    public void setRecords(List<MedicalRecord> records) {
        this.records = records;
    }
}
