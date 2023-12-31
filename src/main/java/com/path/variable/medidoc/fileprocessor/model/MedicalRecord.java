package com.path.variable.medidoc.fileprocessor.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {

    @Id
    @GeneratedValue(generator= "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Nonnull
    private String externalId;

    @Nonnull
    private String externalIdType;

    @Nonnull
    @Lob
    @Column(name="file_contents", columnDefinition="LONGTEXT NOT NULL")
    private String fileContents;

    @Nonnull
    private String fileFormat;

    @CreationTimestamp
    private ZonedDateTime creationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }

    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }
}
