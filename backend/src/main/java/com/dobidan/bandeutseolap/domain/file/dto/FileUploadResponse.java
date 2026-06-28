package com.dobidan.bandeutseolap.domain.file.dto;

import java.time.LocalDateTime;

/**
 * FileUploadResponse
 *
 * 파일 업로드 응답 DTO.
 * 게시글 등록/수정 시 업로드된 파일 정보를 반환.
 */

public record FileUploadResponse(Long fileId,
                                 String originFileName,
                                 String storedFileName,
                                 String storageKey,
                                 String fileExt,
                                 String mimeType,
                                 Long fileSize,
                                 String fileStatusCd,
                                 LocalDateTime uploadedAt) {
}
