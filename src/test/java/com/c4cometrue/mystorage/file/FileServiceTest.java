package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.FileDeleteRequest;
import com.c4cometrue.mystorage.dto.FileDownloadRequest;
import com.c4cometrue.mystorage.dto.FileUploadRequest;
import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("파일 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileServiceTest {
	@Mock
	private FileDataAccessService fileDataAccessService;

	@InjectMocks
	private FileService fileService;

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(fileService, "storagePath", "/path/to/storage");
		ReflectionTestUtils.setField(fileService, "bufferSize", 1024);
	}

	@Test
	@DisplayName("업로드 실패 테스트")
	void uploadFileFailTest() throws IOException {
		FileUploadRequest request = FileUploadRequest.of(mockMultipartFile, userId);
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.uploadFile(request)
		);

		assertEquals(ErrorCode.FILE_COPY_ERROR, thrown.getCode());
	}

	@Test
	@DisplayName("다운로드 실패 테스트")
	void downloadFileFailTest() throws IOException {
		FileDownloadRequest request = mock(FileDownloadRequest.class);
		when(request.fileId()).thenReturn(fileId);
		when(request.userId()).thenReturn(userId);
		when(request.userPath()).thenReturn(userPath);

		Metadata mockMetadata = METADATA;
		when(fileDataAccessService.findBy(anyLong())).thenReturn(mockMetadata);

		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.downloadFile(request)
		);
		assertEquals(ErrorCode.FILE_COPY_ERROR, thrown.getCode());
	}

	@Test
	@DisplayName("삭제 실패 테스트")
	void deleteFileFailTest() throws IOException {
		FileDeleteRequest request = FileDeleteRequest.of(fileId, userId);
		when(fileDataAccessService.findBy(fileId)).thenReturn(METADATA);
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.deleteFile(request)
		);

		assertEquals(ErrorCode.FILE_DELETE_ERROR, thrown.getCode());
	}

	@Test
	@DisplayName("업로드 IO 실패 테스트")
	void uploadFileIoFailTest() throws IOException {
		MultipartFile mockFile = mock(MultipartFile.class);
		when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(mockMultipartFile.getBytes()));
		when(mockFile.getOriginalFilename()).thenReturn(OriginalFileName);

		FileUploadRequest request = FileUploadRequest.of(mockFile, userId);

		doNothing().when(fileDataAccessService).persist(any(Metadata.class));
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.uploadFile(request)
		);
	}

	@Test
	@DisplayName("감동의 업로드 성공 테스트")
	void uploadFileTest22() throws IOException {
		// given
		var multipartFile = mock(MultipartFile.class);
		var inputStream = mock(InputStream.class);
		var outStream = mock(OutputStream.class);
		var files = mockStatic(Files.class);
		var request = new FileUploadRequest(multipartFile, userId);

		given(multipartFile.getInputStream()).willReturn(inputStream);
		given(multipartFile.getOriginalFilename()).willReturn(OriginalFileName);
		given(Files.newOutputStream(any())).willReturn(outStream);
		given(inputStream.read(any()))
				.willReturn(10)
				.willReturn(20)
				.willReturn(30)
				.willReturn(-1);

		// when
		fileService.uploadFile(request);

		// then
		then(outStream).should(times(3)).write(any(), eq(0), anyInt());

		files.close();
	}
}
