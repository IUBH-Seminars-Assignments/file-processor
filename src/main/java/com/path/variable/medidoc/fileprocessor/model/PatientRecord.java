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
}
