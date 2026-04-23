package com.dobidan.bandeutseolap.domain.file.dto;

/**
 * FileInfoResponse
 *
 * 파일 상세정ㅂ조 응답 DTO.
 * 파일 바이너리 데이터 + 메타정보 포함.
 */

public record FileInfoResponse(
        Long fileId,
        String originFileName,
        String fileExt,
        String mimeType,
        Long fileSize,
        String fileStatusCd) {
}
