package com.path.variable.medidoc.fileprocessor.dto;

import com.path.variable.medidoc.fileprocessor.model.MedicalRecord;

public record IdPair(String externalId, String externalIdType) {

    public static IdPair fromMedicalRecord(MedicalRecord medicalRecord) {
        return new IdPair(medicalRecord.getExternalId(), medicalRecord.getExternalIdType());
    }

}
