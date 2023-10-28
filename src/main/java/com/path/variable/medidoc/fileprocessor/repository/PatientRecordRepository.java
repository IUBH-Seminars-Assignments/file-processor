package com.path.variable.medidoc.fileprocessor.repository;

import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRecordRepository extends JpaRepository<PatientRecord, String> {
}
