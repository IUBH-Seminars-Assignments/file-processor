package com.path.variable.medidoc.fileprocessor.repository;

import com.path.variable.medidoc.fileprocessor.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {
}
