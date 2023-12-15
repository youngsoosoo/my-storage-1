package com.c4cometrue.mystorage.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.service.FileDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/files")
public class FileDataController {

    private final FileDataService fileDataService;

    @PostMapping("") // 파일 업로드를 위한 POST 메서드
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userEmail") String userEmail) {

        try {

            fileDataService.writeFile(file, userEmail);
            log.info("파일 저장 및 메타 데이터 저장 완료");
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            log.error("파일 저장 혹은 메타 데이터 저장 실패" + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteFile(@RequestParam("fileName") String fileName, @RequestParam("userEmail") String userEmail){

        try {

            fileDataService.deleteFileData(fileName, userEmail);
            log.info("파일 삭제 완료");
            return ResponseEntity.ok().build();
        }catch (Exception e) {

            log.error("파일 삭제 실패: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName") String fileName, @RequestParam("userEmail") String userEmail){

        try {

            Resource resource = fileDataService.downloadFile(fileName, userEmail);
            log.info(resource.toString());
            if (resource.exists()){

                return ResponseEntity.ok().header(
                        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            }else{

                log.error("파일 다운로드 실패: 데이터가 없습니다.");
                return ResponseEntity.badRequest().build();
            }

        }catch (Exception e) {

            log.error("파일 다운로드 실패: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
