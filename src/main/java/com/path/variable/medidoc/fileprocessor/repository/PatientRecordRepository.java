package com.path.variable.medidoc.fileprocessor.repository;

import com.path.variable.medidoc.fileprocessor.model.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRecordRepository extends JpaRepository<PatientRecord, String> {

    Optional<PatientRecord> findByRecords_ExternalId(String externalId);

}
