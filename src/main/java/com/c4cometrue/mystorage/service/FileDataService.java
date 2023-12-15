package com.c4cometrue.mystorage.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
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
        file.transferTo(path);

        saveFileData(formattedDateTime + "_" + file.getOriginalFilename(), file.getContentType(), file.getSize(), UPLOADED_FOLDER, userEmail);
    }

    // 파일 메타 데이터 저장
    private void saveFileData(String fileName, String fileType, Long fileSize, String filePath, String userEmail){

        FileData fileData = dtoToEntity(fileName, fileType, fileSize, filePath, userEmail);
        fileData.setStatus(Status.ACTIVE);
        fileDataRepository.save(fileData);
    }

    public void deleteFileData(String fileName, String userEmail) throws IOException {

        log.info("fileName: " + fileName);
        FileData fileData = fileDataRepository.findByFileNameAndUserEmail(fileName, userEmail);

        if (fileData != null){
            fileData.setStatus(Status.DELETED);
            fileDataRepository.save(fileData);

            Path path = Paths.get(UPLOADED_FOLDER + fileName);
            Files.delete(path);
        }else{
            throw new RuntimeException("파일 없음");
        }
    }

    public Resource downloadFile(String fileName, String userEmail) throws MalformedURLException {

        FileData fileData = fileDataRepository.findByFileNameAndUserEmail(fileName, userEmail);

        if (fileData != null && Objects.equals(fileData.getUserEmail(), userEmail)){

            Path filePath = Paths.get(UPLOADED_FOLDER).resolve(fileName).normalize();
            log.info(filePath);
            return new UrlResource(filePath.toUri());
        }else{
            throw new RuntimeException("사용자가 다릅니다.");
        }
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
