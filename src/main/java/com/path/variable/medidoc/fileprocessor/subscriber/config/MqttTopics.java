package com.path.variable.medidoc.fileprocessor.subscriber.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttTopics {

    private final String fileUploadTopic;

    private final String idSearchTopic;

    private final String recordSearchTopic;

    private final String idResultsTopic;

    private final String recordResultsTopic;

    public MqttTopics(@Value("${file.upload.topic}") String fileUploadTopic, @Value("${id.search.topic}") String idSearchTopic, @Value("${record.search.topic}") String recordSearchTopic,
                      @Value("${id.results.topic}") String idResultsTopic, @Value("${record.results.topic}") String recordResultsTopic){
        this.fileUploadTopic = fileUploadTopic;
        this.idSearchTopic = idSearchTopic;
        this.recordSearchTopic = recordSearchTopic;
        this.idResultsTopic = idResultsTopic;
        this.recordResultsTopic = recordResultsTopic;
    }

    public String getFileUploadTopic() {
        return fileUploadTopic;
    }

    public String getIdSearchTopic() {
        return idSearchTopic;
    }

    public String getRecordSearchTopic() {
        return recordSearchTopic;
    }

    public String getIdResultsTopic() {
        return idResultsTopic;
    }

    public String getRecordResultsTopic() {
        return recordResultsTopic;
    }
}
