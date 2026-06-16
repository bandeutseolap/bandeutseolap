package com.dobidan.bandeutseolap.domain.file.service;

import com.dobidan.bandeutseolap.domain.file.dto.FileRequestDTO;
import com.dobidan.bandeutseolap.domain.file.dto.FileUploadResponse;
import com.dobidan.bandeutseolap.domain.file.entity.AppFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelBoardFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelDocumentFile;
import com.dobidan.bandeutseolap.domain.file.entity.RelProjectFile;
import com.dobidan.bandeutseolap.domain.file.repository.AppFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelBoardFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelDocumentFileRepository;
import com.dobidan.bandeutseolap.domain.file.repository.RelProjectFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // jakarta 대신 스프링 트랜잭션 권장
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

import static org.springframework.util.ReflectionUtils.setField;

/**
 * FileCommonService
 * - 기존 FileServiceImpl 코드를 1도 건드리지 않기 위해 단독 분리한 공통 인프라 서비스
 * - 기획서의 데이터 Flow 5단계 및 상태 전이(PENDING -> UPLOADED -> ATTACHED)를 전담 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileCommonService {

    private final AppFileRepository appFileRepository;
    private final RelBoardFileRepository relBoardFileRepository;
    private final RelDocumentFileRepository relDocumentFileRepository;
    private final RelProjectFileRepository relProjectFileRepository;

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * - @Transactional을 걸어 원장 등록(PENDING) -> 물리 저장 -> 관계 매핑(ATTACHED)을 단일 원자성 트랜잭션으로 통제
     */
    @Transactional(rollbackFor = Exception.class)
    public List<FileUploadResponse> processUnifiedUpload(FileRequestDTO requestDTO){

        List<FileUploadResponse> responses = new ArrayList<>();

        // 넘어온 파일 리스트가 없으면 즉시 종료
        if (requestDTO.files() == null || requestDTO.files().isEmpty()){
            return responses;
        }

        // 1단계 : domainType에 따른 저장 폴더 경로 격리
        String domainFolder = requestDTO.domainType().toLowerCase();
        String targetDir = uploadPath + "/" + domainFolder + "/" + requestDTO.ownerId();

        File dir = new File(targetDir);
        if (!dir.exists()) {
            boolean isCreated = dir.mkdirs();
            if (!isCreated) {
                log.error("[권한 에러] physic-file 내부에 폴더를 개설할 수 없습니다. 경로: {}", targetDir);
            }
        }

        // 성공한 파일 ID들을 임시 보관할 바구니
        List<Long> savedFileIds = new ArrayList<>();

        for (MultipartFile file : requestDTO.files()) {
            if (file.isEmpty()) continue;

            // 오리지널 파일명 획득 및 확장자 잘라내기
            String originName = file.getOriginalFilename();
            String fileExt = getExtension(originName);

            // 인프라 보호용 파일 사이즈 및 확장자 위험 검증
            validateFile(file, fileExt);

            // UUID를 활용한 고유 저장 파일명 및 스토리지 키 생성 포맷팅 컨벤션 계승
            String storedFileName = UUID.randomUUID() + "." + fileExt;
            String storageKey = targetDir + "/" + storedFileName;

            // 파일 위변조 검증용 고속 SHA-256 해시값 연산 호출
            String fileHash = getFileHash(file); // 팀원 코드 100% 재활용

            // -----------------------------------------------------------------
            // app_file 테이블 선제 저장 (초기 상태: PENDING)
            // -----------------------------------------------------------------
            AppFile appFile = AppFile.builder()
                    .storageKey(storageKey)
                    .originFileName(originName)
                    .storedFileName(storedFileName)
                    .fileExt(fileExt)
                    .mimeType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileHash(fileHash)
                    .fileStatusCd("PENDING") // PENDING 마킹
                    .uploadedBy(requestDTO.uploadedBy())
                    .build();

            AppFile savedFile = appFileRepository.save(appFile);
            savedFileIds.add(savedFile.getFileId()); // 바구니에 ID 누적

            // -----------------------------------------------------------------
            // 실제 하드디스크 스토리지 물리 저장
            // -----------------------------------------------------------------
            try {
                Path path = Paths.get(storageKey);
                Files.write(path, file.getBytes());

                // 물리 저장 완결 확인 후 상태 업그레이드 (UPLOADED)
                updateEntityStatus(savedFile, "UPLOADED");
                appFileRepository.save(savedFile);

            } catch (IOException e) {
                // 물리 파일 누수 가능성을 위한 트랜잭션 배수진 마킹 정책
                log.error("[통합 업로드 장애] 물리 저장 실패 - 파일명: {}, 상태 FAILED 마킹 후 트랜잭션 롤백", originName);
                updateEntityStatus(savedFile, "FAILED"); // 데이터 정합성 지표: FAILED 마킹
                appFileRepository.save(savedFile);

                // 스프링에게 가짜 예외를 가공하여 위로 던짐으로써 상단 DB 인서트 내역까지 통째로 무효화(Rollback) 유도
                throw new RuntimeException("물리 파일 저장 실패로 트랜잭션을 전체 취소합니다.", e);
            }

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

        // -----------------------------------------------------------------
        // 기획 Flow 5: 다형성 다이렉트 도메인 관계 정보 DB(Rel) 매핑 라우팅
        // -----------------------------------------------------------------
        mapToRequestDomainRelations(requestDTO, savedFileIds);

        // -----------------------------------------------------------------
        // 최종 완료: 관계 테이블 이관까지 100% 성공한 경우 최종 ATTACHED 종지부 마킹
        // -----------------------------------------------------------------
        for (Long fileId : savedFileIds) {
            appFileRepository.findById(fileId).ifPresent(file -> {
                updateEntityStatus(file, "ATTACHED"); //ATTACHED
                appFileRepository.save(file);
            });
        }

        return responses;
    }

    /**
     * domainType 변수값 기반의 전략적 업무 도메인 분기 처리 라우터
     * 💡 [수정 포인트] 파라미터 변수명(requestDTO, savedFileIds)에 맞춰 내부 람다/메서드 호출 인자명을 수정했습니다.
     */
    private void mapToRequestDomainRelations(FileRequestDTO requestDTO, List<Long> savedFileIds) {
        String domain = requestDTO.domainType().toUpperCase();
        log.info("[Domain Routing] 업무 관계 엔티티 매핑 라우팅 시작 -> Target: {}", domain);

        for (Long fileId : savedFileIds) {
            switch (domain) {
                case "BOARD" -> {
                    RelBoardFile rel = createRelationInstance(RelBoardFile.class, "boardId", requestDTO.ownerId(), fileId, requestDTO.fileTypeCd(), requestDTO.uploadedBy());
                    relBoardFileRepository.save(rel);
                }
                case "DOCUMENT" -> {
                    RelDocumentFile rel = createRelationInstance(RelDocumentFile.class, "documentId", requestDTO.ownerId(), fileId, requestDTO.fileTypeCd(), requestDTO.uploadedBy());
                    relDocumentFileRepository.save(rel);
                }
                case "PROJECT" -> {
                    RelProjectFile rel = createRelationInstance(RelProjectFile.class, "projectId", requestDTO.ownerId(), fileId, requestDTO.fileTypeCd(), requestDTO.uploadedBy());
                    relProjectFileRepository.save(rel);
                }
                default -> throw new IllegalArgumentException("올바르지 않거나 지원하지 않는 업무 도메인 타입입니다: " + domain);
            }
        }
    }

    /**
     * AppFile 엔티티 내부의 상태값(fileStatusCd)을 Setter 없이 강제로 주입해 주는 내부 유틸리티
     */
    private void updateEntityStatus(AppFile savedFile, String status) {
        try {
            java.lang.reflect.Field field = AppFile.class.getDeclaredField("fileStatusCd");
            field.setAccessible(true);
            setField(field, savedFile, status);
        } catch (Exception e) {
            log.error("AppFile 상태 필드 주입 중 예외 발생", e);
        }
    }

    /**
     * 캡슐화 장벽(private)을 뚫고 데이터를 삽입하는 핵심 자바 리플렉션 객체 생성 로직
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
            throw new RuntimeException(clazz.getSimpleName() + " 인스턴스 관계 매핑 빌드 중 장애 발생", e);
        }
    }

    private String getFileHash(MultipartFile file) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return  hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("파일 해시 생성 실패");
        }
    }

    private void validateFile(MultipartFile file, String ext) {
        long maxBytes = 100 * 1024 * 1024 ;

        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("단건 업로드 제한 용량(100MB)을 초과했습니다.");
        }

        if (List.of("exe", "sh", "bat", "jsp", "php").contains(ext.toLowerCase())) {
            throw new IllegalArgumentException("보안 정책상 해당 악성 위험 확장자는 업로드가 금지되어 있습니다: " + ext);
        }
    }

    private String getExtension(String originName) {
        if (originName == null || !originName.contains(".")) return "";
        return originName.substring(originName.lastIndexOf(".") + 1);
    }
}