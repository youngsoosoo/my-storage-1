package com.c4cometrue.mystorage.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.service.FileService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;



@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFileRes uploadFile(
            @NotNull @RequestPart MultipartFile file,
            @NotBlank @RequestPart String username
    ) {
        return fileService.uploadFile(file, username);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(
            @NotBlank @RequestParam String fileStorageName,
            @NotBlank @RequestParam String username
    ) {
        fileService.deleteFile(fileStorageName, username);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Resource downloadFile(
            @NotBlank @RequestParam String fileStorageName,
            @NotBlank @RequestParam String username
    ) {
        return fileService.downloadFile(fileStorageName, username);
    }
}
