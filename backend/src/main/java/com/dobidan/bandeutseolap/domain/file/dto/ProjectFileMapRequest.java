package com.dobidan.bandeutseolap.domain.file.dto;

public record ProjectFileMapRequest(
        Long fileId,       // 연결할 파일의 고유 ID (PK)
        String fileRoleCd  // 파일의 역할 코드 (예: ATTACHMENT, RESULT)
) {}
