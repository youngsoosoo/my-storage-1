package com.c4cometrue.mystorage.entity;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.c4cometrue.mystorage.util.entity.BaseEntity;
import com.c4cometrue.mystorage.util.entity.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FileData extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    private String userEmail;


    @Builder
    private FileData(String fileName, String fileType, Long fileSize, String filePath, String userEmail){
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.userEmail = userEmail;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
