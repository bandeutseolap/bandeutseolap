package com.dobidan.bandeutseolap.domain.file.dto;

/**
 * FileDownloadResponse
 *
 * 파일 다운로드 응답 DTO.
 * 파일 바이너리 데이터 + 메타정보 포함.
 */

public record FileDownloadResponse(
        byte[] data,
        String originFileName,
        String mimeType
) {
}
