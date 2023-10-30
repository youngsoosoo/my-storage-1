package com.c4cometrue.mystorage.file;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.FileDeleteRequest;
import com.c4cometrue.mystorage.dto.FileDownloadRequest;
import com.c4cometrue.mystorage.dto.FileUploadRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void uploadFile(FileUploadRequest fileUploadRequest) {
		fileService.uploadFile(fileUploadRequest.multipartFile(), fileUploadRequest.userId());
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFile(FileDeleteRequest request) {
		fileService.deleteFile(request.fileId(), request.userId());
	}

	@GetMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void downloadFile(FileDownloadRequest request) {
		fileService.downloadFile(request.fileId(), request.userPath(), request.userId());
	}

}