package com.dobidan.bandeutseolap.domain.file.dto;

public record BoardFileMapRequest(
        Long fileId,    // 연결할 파일의 고유 ID (PK)
        String fileRoleCd   // 파일의 역할 코드 (예: ATTACHMENT, THUMBNAIL)
) {
}
