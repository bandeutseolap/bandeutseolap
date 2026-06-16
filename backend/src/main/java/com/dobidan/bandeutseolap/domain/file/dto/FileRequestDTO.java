package com.dobidan.bandeutseolap.domain.file.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;


public record FileRequestDTO(
        String domainType,        // BOARD, PROJECT, DOCUMENT 등 분기용 키값
        Long ownerId,             // board_id, project_id 등 도메인 식별 PK
        Long projectId,           // 프로젝트 하위에 속한 경우만 (NULLABLE)
        String fileTypeCd,        // IMAGE, ATTACH, PROFILE 등 역할 코드
        Long uploadedBy,          // 업로드 요청자 USER_ID
        List<MultipartFile> files // 업로드 대상 실제 바이너리 파일 목록
) {}