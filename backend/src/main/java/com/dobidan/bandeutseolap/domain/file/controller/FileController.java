package com.dobidan.bandeutseolap.domain.file.controller;

import com.dobidan.bandeutseolap.domain.file.dto.FileDownloadResponse;
import com.dobidan.bandeutseolap.domain.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "File", description = "파일 관련 API (파일 다운로드/삭제 등)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/files")
public class FileController {

    private final FileService fileService;

    //파일 다운로드
    @Operation(summary = "파일 다운로드", description = "게시글번호/파일번호로 조회하여 다운로드")
    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long boardId,
            @PathVariable Long fileId) {

        FileDownloadResponse response = fileService.downloadFile(boardId, fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.originFileName() + "\"")
                .body(response.data());
    }
}
