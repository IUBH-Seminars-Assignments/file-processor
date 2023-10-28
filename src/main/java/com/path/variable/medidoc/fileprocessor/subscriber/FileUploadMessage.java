package com.path.variable.medidoc.fileprocessor.subscriber;

public record FileUploadMessage(String id, String idName, String filePayload, String payloadFormat) {}
