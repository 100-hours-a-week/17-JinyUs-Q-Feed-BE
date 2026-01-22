package com.ktb.file.service.impl;

import com.ktb.file.domain.File;
import com.ktb.file.domain.FileCategory;
import com.ktb.file.domain.StorageType;
import com.ktb.file.dto.FileUploadResult;
import com.ktb.file.exception.FileInvalidMetadataException;
import com.ktb.file.exception.FileSizeExceededException;
import com.ktb.file.repository.FileRepository;
import com.ktb.file.service.FileUploadService;
import com.ktb.file.util.SizeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 파일 업로드 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    // ==================== 상수 정의 ====================

    private static final String ERROR_MESSAGE_FILE_EMPTY = "파일이 비어있습니다";
    private static final String ERROR_MESSAGE_MIME_TYPE_UNKNOWN = "MIME 타입을 확인할 수 없습니다";
    private static final String ERROR_MESSAGE_EXTENSION_NOT_FOUND = "파일 확장자를 찾을 수 없습니다";
    private static final String TEMP_URL_PREFIX = "https://example.com";

    // ==================== 의존성 ====================

    private final FileRepository fileRepository;

    // TODO: S3 또는 파일 저장소 클라이언트 주입 필요
    // private final S3Client s3Client;

    // ==================== 공개 메서드 ====================

    /**
     * 파일을 업로드하고 저장합니다.
     *
     * @param file 업로드할 파일
     * @param category 파일 카테고리 (AUDIO, VIDEO 등)
     * @return 업로드 결과 (URL, 파일명, 크기, MIME 타입)
     * @throws FileInvalidMetadataException 파일이 비어있거나 메타데이터가 유효하지 않을 때
     * @throws FileSizeExceededException 파일 크기가 제한을 초과할 때
     */
    @Override
    @Transactional
    public FileUploadResult upload(MultipartFile file, FileCategory category) {
        validateFormat(file, category);
        validateSize(file, category);

        String extension = extractExtension(file.getOriginalFilename());
        String storedName = generateStoredName(extension);

        // TODO: S3 업로드 후 실제 URL 받기
        String tempPath = buildFilePath(category, storedName);
        String tempUrl = TEMP_URL_PREFIX + tempPath;

        File fileEntity = File.create(
                file.getOriginalFilename(),
                storedName,
                tempPath,
                extension,
                file.getSize(),
                file.getContentType(),
                category,
                StorageType.LOCAL  // TODO: S3 업로드 시 StorageType.S3로 변경
        );

        File savedFile = fileRepository.save(fileEntity);

        log.info("File uploaded - ID: {}, Category: {}, Size: {}, URL: {}",
                savedFile.getId(), category, savedFile.getReadableSize(), tempUrl);

        return new FileUploadResult(
                tempUrl,
                savedFile.getOriginalName(),
                savedFile.getSize(),
                savedFile.getMimeType()
        );
    }

    /**
     * 파일 형식을 검증합니다.
     *
     * @param file 검증할 파일
     * @param category 파일 카테고리
     * @throws FileInvalidMetadataException MIME 타입 또는 확장자가 허용 목록에 없는 경우
     */
    @Override
    public void validateFormat(MultipartFile file, FileCategory category) {
        if (file == null || file.isEmpty()) {
            throw new FileInvalidMetadataException(ERROR_MESSAGE_FILE_EMPTY);
        }

        String mimeType = file.getContentType();
        if (mimeType == null) {
            throw new FileInvalidMetadataException(ERROR_MESSAGE_MIME_TYPE_UNKNOWN);
        }

        if (!category.isAllowedMimeType(mimeType)) {
            throw new FileInvalidMetadataException(
                    "허용되지 않은 파일 형식입니다. 허용 형식: "
                            + category.getAllowedMimeTypesAsString()
            );
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!category.isAllowedExtension(extension)) {
            throw new FileInvalidMetadataException(
                    "허용되지 않은 확장자입니다. 허용 확장자: "
                            + category.getAllowedExtensionsAsString()
            );
        }
    }

    /**
     * 파일 크기를 검증합니다.
     *
     * @param file 검증할 파일
     * @param category 파일 카테고리
     * @throws FileSizeExceededException 파일 크기가 제한을 초과한 경우
     */
    @Override
    public void validateSize(MultipartFile file, FileCategory category) {
        if (file == null || file.isEmpty()) {
            throw new FileInvalidMetadataException(ERROR_MESSAGE_FILE_EMPTY);
        }

        long maxSize = category.getMaxSizeBytes();
        if (file.getSize() > maxSize) {
            throw new FileSizeExceededException(category, SizeUtil.getReadableSize(file.getSize()));
        }
    }

    // ==================== 내부 헬퍼 메서드 ====================

    /**
     * 파일명에서 확장자를 추출합니다.
     *
     * @param originalFilename 원본 파일명
     * @return 확장자 (소문자)
     * @throws FileInvalidMetadataException 확장자를 찾을 수 없을 때
     */
    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new FileInvalidMetadataException(ERROR_MESSAGE_EXTENSION_NOT_FOUND);
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * UUID 기반 저장 파일명을 생성합니다.
     *
     * @param extension 파일 확장자
     * @return UUID + 확장자 형식의 파일명
     */
    private String generateStoredName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * 카테고리별 파일 경로를 생성합니다.
     *
     * @param category 파일 카테고리
     * @param storedName 저장 파일명
     * @return 파일 경로
     */
    private String buildFilePath(FileCategory category, String storedName) {
        return "/uploads/" + category.name().toLowerCase() + "/" + storedName;
    }
}
