package com.dobidan.bandeutseolap.domain.file.service;

import com.dobidan.bandeutseolap.domain.file.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * FileService
 *
 * 파일 업로드/다운로드/삭제 비즈니스 로직 인터페이스.
 */

public interface FileService {
    // 파일 업로드
    List<FileUploadResponse> uploadFiles(List<MultipartFile> files, Long boardId, Long userId);
}