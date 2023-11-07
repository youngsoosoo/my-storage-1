package com.c4cometrue.mystorage.file;

import com.c4cometrue.mystorage.file.entity.FileMetaData;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class TestMockFile {

	public static final MultipartFile mockMultipartFile = mock(MultipartFile.class);

	public static final String mockRoot = "/Users/sinhyeyeon/Desktop/storage/";

	public static final Path mockRootPath = Paths.get("/Users/sinhyeyeon/Desktop/storage/");
	public static final String mockDownRootPath = "/Users/sinhyeyeon/Desktop/storage_down/";
	public static final Long mockFileId = 1L;
	public static final String mockUserName = "userName";
	public static final String mockFileName = "test.txt";
	public static final String mockFilePath = UUID.randomUUID() + mockFileName;
	public static final Long mockSize = 100L;
	public static final String mockContentType = "text/plain";
	public static final FileMetaData mockFileMetaData =
		FileMetaData.builder()
			.fileName(mockFileName)
			.savedPath(mockFilePath)
			.fileSize(mockSize)
			.userName(mockUserName)
			.fileMine(mockContentType)
			.build();
	public static final Path mockUploadPath = mockRootPath.resolve(mockFilePath);
	public static final Path mockDeletePath = Paths.get(mockFilePath).toAbsolutePath();
	public static final Path mockStoragePath = mockRootPath.resolve(mockFilePath);
	public static final Path mockDownloadPath = Path.of(mockDownRootPath).resolve(mockFileName);

	public static final int mockBufferSize = 1024;
	public static final int mockReadCnt = 0;

	public static final byte mockBuffer[] = new byte[mockBufferSize];

}