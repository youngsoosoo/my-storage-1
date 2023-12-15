package com.c4cometrue.mystorage.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.entity.FileData;
import com.c4cometrue.mystorage.repository.FileDataRepository;
import com.c4cometrue.mystorage.util.entity.Status;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileDataService {

    private final FileDataRepository fileDataRepository;
    private static final String UPLOADED_FOLDER = "src/main/resources/";


    // 파일 서버에 저장
    public void writeFile(MultipartFile file, String userEmail) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(formatter);
        Path path = Paths.get(UPLOADED_FOLDER + formattedDateTime + "_" + file.getOriginalFilename());
        log.info("path: " + path);
        file.transferTo(path);

        saveFileData(formattedDateTime + "_" + file.getOriginalFilename(), file.getContentType(), file.getSize(), UPLOADED_FOLDER, userEmail);
    }

    // 파일 메타 데이터 저장
    private void saveFileData(String fileName, String fileType, Long fileSize, String filePath, String userEmail){

        FileData fileData = dtoToEntity(fileName, fileType, fileSize, filePath, userEmail);
        fileData.setStatus(Status.ACTIVE);
        fileDataRepository.save(fileData);
    }

    private FileData dtoToEntity(String fileName, String fileType, Long fileSize, String filePath, String userEmail){

        return FileData.builder()
            .fileName(fileName)
            .filePath(filePath)
            .fileType(fileType)
            .fileSize(fileSize)
            .userEmail(userEmail)
            .build();
    }

}
