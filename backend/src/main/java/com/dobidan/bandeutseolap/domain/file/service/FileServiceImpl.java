package com.dobidan.bandeutseolap.domain.file.service;


import com.dobidan.bandeutseolap.domain.file.dto.FileUploadResponse;
import com.dobidan.bandeutseolap.domain.file.entity.AppFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelBoardFile;
import com.dobidan.bandeutseolap.domain.file.repository.AppFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelBoardFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FileServiceImpl
 *
 * FileService 인터페이스 구현체.
 * - 업로드 프로세스: UPLOADING → 물리 저장 → rel 저장 → ACTIVE
 * - 삭제 정책: 논리 삭제 (file_status_cd = DELETED)
 * - 물리 삭제: 스케줄러가 delete_scheduled_at 기준으로 별도 처리
 */

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AppFileRepository appFileRepository;
    private final RelBoardFileRepository relBoardFileRepository;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    @Transactional
    public List<FileUploadResponse> uploadFile(List<MultipartFile> files, Long boardId, Long userId) {

        List<FileUploadResponse> responses = new ArrayList<>();

        for(MultipartFile file: files) {
            if (file.isEmpty()) continue;

            // 1. 파일 정보 추출
            String originFileName = file.getOriginalFilename();
            String fileExt        = getExtension(originFileName);
            String storedFileName = UUID.randomUUID() + "." + fileExt;
            String storageKey     = uploadPath + "/" + boardId + "/" + storedFileName;
            String mimeType       = file.getContentType();
            Long   fileSize       = file.getSize();
            String fileHash       = getFileHash(file);

            // 2. app_file table INSERT (UPLOADING)
            AppFile appFile = AppFile.builder()
                    .storageKey(storageKey)
                    .originFileName(originFileName)
                    .storedFileName(storedFileName)
                    .fileExt(fileExt)
                    .mimeType(mimeType)
                    .fileSize(fileSize)
                    .fileHash(fileHash)
                    .fileStatusCd("UPLOADING")
                    .uploadedBy(userId)
                    .build();

            AppFile savedFile = appFileRepository.save(appFile);

            // 3. 물리 파일 저장
            try {
                String dirPath = uploadPath + "/" + boardId;
                File dir = new File(dirPath);
                if (!dir.exists()) dir.mkdirs();

                Path path = Paths.get(storageKey);
                Files.write(path, file.getBytes());
            } catch (IOException e){
                throw new RuntimeException("file uploading error!!: " + originFileName);
            }

            // 4. rel_board_file table INSERT
            RelBoardFile relBoardFile = RelBoardFile.builder()
                    .boardId(boardId)
                    .fileId(savedFile.getFileId())
                    .fileRoleCd("ATTACHMENT")
                    .attachedBy(userId)
                    .build();

            relBoardFileRepository.save(relBoardFile);

            // 5. app_file_UPDATE (ACTIVE)
            savedFile.fileActivate();
            appFileRepository.save(savedFile);

            responses.add(new FileUploadResponse(
                    savedFile.getFileId(),
                    savedFile.getOriginFileName(),
                    savedFile.getStoredFileName(),
                    savedFile.getStorageKey(),
                    savedFile.getFileExt(),
                    savedFile.getMimeType(),
                    savedFile.getFileSize(),
                    savedFile.getFileStatusCd(),
                    savedFile.getUploadedAt()
                    ));
        }

        return responses;
    }

    // 파일 확장자 추출
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    // 파일 해시값 생성(SHA-256)
    private String getFileHash(MultipartFile file) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x",b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("파일 해시 생성 실패");
        }
    }
}
