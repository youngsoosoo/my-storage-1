package com.c4cometrue.mystorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record UpdateFolderNameReq(
	@NotNull long folderId,
	@NotNull long parentFolderId,
	@NotBlank(message = "user name is blank") String userName,
	@NotBlank(message = "new folder name is blank") String newFolderName
) {
}
