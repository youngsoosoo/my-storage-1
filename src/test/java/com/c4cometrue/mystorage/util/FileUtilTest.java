package com.c4cometrue.mystorage.util;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class FileUtilTest {
	private static MockedStatic<Files> fileMockedStatic;

	// fileMockedStatic 공통 사용 부분이 많아 BeforeAll에서 BeforeEach로 변경
	@BeforeEach
	public void setup() {
		fileMockedStatic = mockStatic(Files.class);
	}

	@AfterEach
	public void tearDown() {
		fileMockedStatic.close();
	}

	@Test
	@DisplayName("파일 업로드 성공")
	void uploadFile() throws IOException {
		// given
		var inputStream = mock(InputStream.class);

		given(mockMultipartFile.isEmpty()).willReturn(false);
		given(mockMultipartFile.getInputStream()).willReturn(inputStream);

		// when
		FileUtil.uploadFile(mockMultipartFile, mockStoragePath);

		// then
		fileMockedStatic.verify(() -> Files.copy(inputStream, mockStoragePath), times(1));
	}

	@Test
	@DisplayName("파일 업로드 실패 - 파일이 비어있는 경우")
	void uploadFileFailFileEmpty() {
		// given
		given(mockMultipartFile.isEmpty()).willReturn(true);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> FileUtil.uploadFile(mockMultipartFile, mockStoragePath));

		// then
		assertEquals(ErrorCd.INVALID_FILE.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 업로드 실패 - 파일 처리 과정에서 문제가 발생하는 경우")
	void uploadFileFailProcess() throws IOException {
		// given
		var inputStream = mock(InputStream.class);

		given(mockMultipartFile.isEmpty()).willReturn(false);
		given(mockMultipartFile.getInputStream()).willReturn(inputStream);
		given(Files.copy(inputStream, mockStoragePath)).willThrow(IOException.class);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> FileUtil.uploadFile(mockMultipartFile, mockStoragePath));

		// then
		assertEquals(ErrorCd.INTERNAL_SERVER_ERROR.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		given(Files.exists(mockStoragePath)).willReturn(true);

		// when
		FileUtil.deleteFile(mockStoragePath);

		// then
		fileMockedStatic.verify(() -> Files.delete(mockStoragePath), times(1));
	}

	@Test
	@DisplayName("파일 삭제 실패 - 파일 없음")
	void deleteFileFailFileEmpty() {
		// given
		given(Files.exists(mockStoragePath)).willReturn(false);

		// when
		var exception = assertThrows(ServiceException.class, () -> FileUtil.deleteFile(mockStoragePath));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 삭제 실패 - 파일 삭제 과정에서 문제 발생")
	void deleteFileFailProcess() {
		// given
		var invalidPath = Path.of("invalidPath");
		given(Files.exists(invalidPath)).willReturn(true);
		fileMockedStatic.when(() -> Files.delete(invalidPath)).thenThrow(IOException.class); // void라서 given 불가

		// when
		var exception = assertThrows(ServiceException.class, () -> FileUtil.deleteFile(invalidPath));

		// then
		assertEquals(ErrorCd.INTERNAL_SERVER_ERROR.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("폴더 생성")
	void createFolder() {
		// given
		var folderPath = Path.of(mockRootPath).resolve(mockUserName);

		FileUtil.createFolder(folderPath);

		fileMockedStatic.verify(() -> Files.createDirectory(folderPath), times(1));
	}

	@Test
	@DisplayName("폴더 생성 실패 - IOException")
	void createFolderFailProcess() throws IOException {
		// given
		var folderPath = Path.of(mockRootPath).resolve(mockUserName);
		given(Files.createDirectory(folderPath)).willThrow(IOException.class);

		// when
		var exception = assertThrows(ServiceException.class, () -> FileUtil.createFolder(folderPath));

		assertEquals(ErrorCd.INTERNAL_SERVER_ERROR.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("폴더 이름 변경")
	void renameFolder() {
		// given
		Path rootPath = Path.of(mockRootPath);
		var oldFolderPath = rootPath.resolve(mockUserName).resolve("old_name");
		var newFolderPath = rootPath.resolve(mockUserName).resolve("new_name");
		given(Files.exists(oldFolderPath)).willReturn(true);

		FileUtil.renameFolder(oldFolderPath, newFolderPath);

		fileMockedStatic.verify(() -> Files.move(oldFolderPath, newFolderPath, StandardCopyOption.REPLACE_EXISTING),
			times(1));
	}

	@Test
	@DisplayName("폴더 이름 변경 실패 - 폴더 없음")
	void renameFolderFailNoFolder() {
		// given
		Path rootPath = Path.of(mockRootPath);
		var oldFolderPath = rootPath.resolve(mockUserName).resolve("old_name");
		var newFolderPath = rootPath.resolve(mockUserName).resolve("new_name");
		given(Files.exists(oldFolderPath)).willReturn(false);

		var exception = assertThrows(ServiceException.class, () -> FileUtil.renameFolder(oldFolderPath, newFolderPath));

		assertEquals(ErrorCd.FOLDER_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("폴더 이름 변경 실패 - IOException")
	void renameFolderFailProcess() throws IOException {
		// given
		Path rootPath = Path.of(mockRootPath);
		var oldFolderPath = rootPath.resolve(mockUserName).resolve("old_name");
		var newFolderPath = rootPath.resolve(mockUserName).resolve("new_name");
		given(Files.exists(oldFolderPath)).willReturn(true);
		given(Files.move(oldFolderPath, newFolderPath, StandardCopyOption.REPLACE_EXISTING)).willThrow(
			IOException.class);

		var exception = assertThrows(ServiceException.class, () -> FileUtil.renameFolder(oldFolderPath, newFolderPath));

		assertEquals(ErrorCd.INTERNAL_SERVER_ERROR.name(), exception.getErrCode());
	}
}
