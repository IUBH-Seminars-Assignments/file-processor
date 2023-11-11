package com.path.variable.medidoc.fileprocessor.dto;

public record FileUploadMessage(String externalId, String externalIdType, String fileContents, String fileFormat) {}
