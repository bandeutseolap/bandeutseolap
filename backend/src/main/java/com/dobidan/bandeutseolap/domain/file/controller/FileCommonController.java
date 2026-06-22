package com.dobidan.bandeutseolap.domain.file.controller;

import com.dobidan.bandeutseolap.domain.file.dto.FileRequestDTO;
import com.dobidan.bandeutseolap.domain.file.dto.FileUploadResponse;
import com.dobidan.bandeutseolap.domain.file.service.FileCommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Tag(name = "File Common Infrastructure", description = "파일 공통 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileCommonController {
    private final FileCommonService fileCommonService;
    @Operation(summary = "전사 통합 파일 업로드 (files)", description = "파라미터값들과 실제 파일들을 폼 데이터로 나누어 수신합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileUploadResponse>> uploadFilesUnified(
            @RequestParam("domainType") String domainType,
            @RequestParam(value = "ownerId",required = false) Long ownerId,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam("fileTypeCd") String fileTypeCd,
            @RequestParam(value = "uploadedBy",required = false) Long uploadedBy,
            @Parameter(description = "업로드할 실제 파일 리스트") @RequestPart("files") List<MultipartFile> files
    ) {

        log.info("[통합 파일 플랫폼 수신] 도메인: {}, 식별 ID: {}, 유저: {}", domainType, ownerId, uploadedBy);

        // 원래의 DTO 형태로 생성
        FileRequestDTO requestDTO = new FileRequestDTO(
                domainType,
                ownerId,
                projectId,
                fileTypeCd,
                uploadedBy,
                files
        );

        // fileCommonService 호출
        List<FileUploadResponse> responses = fileCommonService.processUnifiedUpload(requestDTO);
        return ResponseEntity.ok(responses);
    }
}
