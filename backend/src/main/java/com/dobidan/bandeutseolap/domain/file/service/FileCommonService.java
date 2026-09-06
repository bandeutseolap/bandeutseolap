package com.dobidan.bandeutseolap.domain.file.service;

import com.dobidan.bandeutseolap.domain.file.dto.FileRequestDTO;
import com.dobidan.bandeutseolap.domain.file.dto.FileUploadResponse;
import com.dobidan.bandeutseolap.domain.file.entity.AppFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelBoardFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelDocumentFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelProjectFile;
import com.dobidan.bandeutseolap.domain.file.event.FileUploadedEvent;
import com.dobidan.bandeutseolap.domain.file.event.PendingFilePayload;
import com.dobidan.bandeutseolap.domain.file.repository.AppFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelBoardFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelDocumentFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelProjectFileRepository;
import com.dobidan.bandeutseolap.domain.user.entity.AppUser;
import com.dobidan.bandeutseolap.domain.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.ReflectionUtils.setField;


/**
 * FileCommonService
 *
 * 파일 업로드 및 업무 도메인(게시판, 문서 등)과의 관계 매핑을 처리하는 공통 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileCommonService {

    private final AppFileRepository appFileRepository;
    private final RelBoardFileRepository relBoardFileRepository;
    private final RelDocumentFileRepository relDocumentFileRepository;
    private final RelProjectFileRepository relProjectFileRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AppUserRepository appUserRepository;

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 통합 파일 업로드 처리
     *
     * 이 메서드는 DB에 PENDING 상태로 메타데이터만 저장하고 커밋한다.
     * 실제 디스크 write는 트랜잭션 커밋 이후 FileUploadedEventListener에서 비동기로 수행되므로,
     * 이 메서드 실행 중 어떤 예외가 나서 롤백되더라도 디스크에는 아무 파일도 남지 않는다.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<FileUploadResponse> processUnifiedUpload(FileRequestDTO requestDTO){

        List<FileUploadResponse> responses = new ArrayList<>();

        if (requestDTO.files() == null || requestDTO.files().isEmpty()){
            return responses;
        }

        Long uploadedBy = appUserRepository.findByLoginId(requestDTO.loginId())
                .map(AppUser::getId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 1. 도메인 유형별 저장 폴더 경로 설정
        String domainFolder = requestDTO.domainType().toLowerCase();

        // 회원가입 등 ID가 없는 경우 오늘 날짜로 폴더를 생성하고, ID가 있으면 ID를 폴더명으로 사용
        String ownerFolder = (requestDTO.ownerId() == null)
                ? LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                : String.valueOf(requestDTO.ownerId());

        // requestDTO.ownerId() 대신 설정된 ownerFolder를 적용하도록 수정
        String targetDir = uploadPath + "/" + domainFolder + "/" + ownerFolder;

        File dir = new File(targetDir);
        if (!dir.exists()) {
            boolean isCreated = dir.mkdirs();
            if (!isCreated) {
                log.error("[오류] 업로드 디렉토리를 생성할 수 없습니다. 경로: {}", targetDir);
            }
        }

        // 등록 성공한 파일 ID를 담을 리스트
        List<Long> savedFileIds = new ArrayList<>();
        List<PendingFilePayload> pendingPayloads = new ArrayList<>();

        for (MultipartFile file : requestDTO.files()) {
            if (file.isEmpty()) continue;

            String originName = file.getOriginalFilename();
            String fileExt = getExtension(originName);

            // 파일 크기 및 확장자 검증
            validateFile(file, fileExt);

            // 고유한 파일명 및 저장 경로(Key) 생성
            String storedFileName = UUID.randomUUID() + "." + fileExt;
            String storageKey = targetDir + "/" + storedFileName;

            // 디스크에 쓰지 않고 바이트만 미리 읽어둔다
            byte[] fileBytes = readBytes(file, originName);

            // 파일 해시값 생성
            String fileHash = getFileHash(file);

            // app_file 테이블 기본 정보 등록 (초기 상태: PENDING)
            AppFile appFile = AppFile.builder()
                    .storageKey(storageKey)
                    .originFileName(originName)
                    .storedFileName(storedFileName)
                    .fileExt(fileExt)
                    .mimeType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileHash(fileHash)
                    .fileStatusCd("PENDING")
                    .uploadedBy(uploadedBy)
                    .build();

            AppFile savedFile = appFileRepository.save(appFile);
            savedFileIds.add(savedFile.getFileId());

            responses.add(toResponse(savedFile));
            pendingPayloads.add(new PendingFilePayload(
                    savedFile.getFileId(), storageKey, targetDir, fileBytes
            ));
        }

        // PROFILE 도메인처럼 중간 관계 테이블(Rel) 매핑이 필요 없는 도메인은 연동 과정을 생략
        if (!"PROFILE".equalsIgnoreCase(requestDTO.domainType())) {
            mapToRequestDomainRelations(requestDTO, savedFileIds, uploadedBy);
        }

        eventPublisher.publishEvent(new FileUploadedEvent(pendingPayloads));

        return responses;
    }

    /**
     * 도메인 유형별 관계 테이블(Rel) 매핑 처리 라우터
     */
    private void mapToRequestDomainRelations(FileRequestDTO requestDTO, List<Long> savedFileIds, Long uploadedBy){
        String domain = requestDTO.domainType().toUpperCase();
        log.info("[관계 매핑] 도메인별 매핑 시작 -> 대상: {}", domain);

        for (Long fileId : savedFileIds) {
            switch (domain) {
                case "BOARD" -> {
                    RelBoardFile rel = createRelationInstance(RelBoardFile.class, "boardId", requestDTO.ownerId(), fileId, requestDTO.fileTypeCd(), uploadedBy);
                    relBoardFileRepository.save(rel);
                }
                case "DOCUMENT" -> {
                    RelDocumentFile rel = createRelationInstance(RelDocumentFile.class, "documentId", requestDTO.ownerId(), fileId, requestDTO.fileTypeCd(), uploadedBy);
                    relDocumentFileRepository.save(rel);
                }
                case "PROJECT" -> {
                    RelProjectFile rel = createRelationInstance(RelProjectFile.class, "projectId", requestDTO.ownerId(), fileId, requestDTO.fileTypeCd(), uploadedBy);
                    relProjectFileRepository.save(rel);
                }
                default -> throw new IllegalArgumentException("지원하지 않는 도메인 타입입니다: " + domain);
            }
        }
    }

    /**
     * 리플렉션을 이용해 동적으로 관계 엔티티 인스턴스를 생성하는 메서드
     */
    private <T> T createRelationInstance(Class<T> clazz, String idFieldName, Long ownerId, Long fileId, String roleCd, Long userId) {
        try {
            java.lang.reflect.Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            java.lang.reflect.Field idField = clazz.getDeclaredField(idFieldName);
            idField.setAccessible(true);
            setField(idField, instance, ownerId);

            java.lang.reflect.Field fileIdField = clazz.getDeclaredField("fileId");
            fileIdField.setAccessible(true);
            setField(fileIdField, instance, fileId);

            java.lang.reflect.Field roleField = clazz.getDeclaredField("fileRoleCd");
            roleField.setAccessible(true);
            setField(roleField, instance, roleCd != null ? roleCd : "ATTACHMENT");

            java.lang.reflect.Field userField = clazz.getDeclaredField("attachedBy");
            userField.setAccessible(true);
            setField(userField, instance, userId);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(clazz.getSimpleName() + " 인스턴스 관계 매핑 중 예외 발생", e);
        }
    }

    private String getFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("파일 해시 생성 실패");
        }
    }

    private void validateFile(MultipartFile file, String ext) {
        long maxBytes = 100 * 1024 * 1024;

        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("단건 업로드 제한 용량(100MB)을 초과했습니다.");
        }

        if (List.of("exe", "sh", "bat", "jsp", "php").contains(ext.toLowerCase())) {
            throw new IllegalArgumentException("보안 정책상 해당 확장자는 업로드가 금지되어 있습니다: " + ext);
        }
    }

    private String getExtension(String originName) {
        if (originName == null || !originName.contains(".")) return "";
        return originName.substring(originName.lastIndexOf(".") + 1);
    }

    private byte[] readBytes(MultipartFile file, String originName){
        try {
            return file.getBytes();
        } catch (IOException e){
          throw new RuntimeException("파일 읽기 실패: " + originName,e);
        }
    }

    private FileUploadResponse toResponse(AppFile savedFile){
        return new FileUploadResponse(
                savedFile.getFileId(),
                savedFile.getOriginFileName(),
                savedFile.getStoredFileName(),
                savedFile.getStorageKey(),
                savedFile.getFileExt(),
                savedFile.getMimeType(),
                savedFile.getFileSize(),
                savedFile.getFileStatusCd(),
                savedFile.getUploadedAt()
        );
    }
}