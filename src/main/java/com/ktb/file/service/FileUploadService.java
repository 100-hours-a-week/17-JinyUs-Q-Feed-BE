package com.ktb.file.service;

import com.ktb.file.domain.FileCategory;
import com.ktb.file.dto.FileUploadResult;
import com.ktb.file.exception.FileInvalidMetadataException;
import com.ktb.file.exception.FileSizeExceededException;
import com.ktb.file.exception.FileStorageMigrationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileUploadService {
    /**
     * 파일을 업로드하고 저장합니다.
     *
     * @param file 업로드할 파일
     * @param category 파일 카테고리 (AUDIO, VIDEO 등)
     * @return 업로드 결과 (URL, 파일명, 크기, MIME 타입)
     * @throws FileInvalidMetadataException 파일이 비어있거나 메타데이터가 유효하지 않을 때
     * @throws FileSizeExceededException 파일 크기가 제한을 초과할 때
     * @throws FileStorageMigrationException 업로드에 실패한 경우
     */
    FileUploadResult upload(MultipartFile file, FileCategory category)
            throws FileInvalidMetadataException, FileSizeExceededException, FileStorageMigrationException;

    /**
     * 파일 형식을 검증합니다.
     *
     * @param file 검증할 파일
     * @param category 파일 카테고리
     * @throws FileInvalidMetadataException MIME 타입 또는 확장자가 허용 목록에 없는 경우
     */
    void validateFormat(MultipartFile file, FileCategory category)
            throws FileInvalidMetadataException;

    /**
     * 파일 크기를 검증합니다.
     *
     * @param file 검증할 파일
     * @param category 파일 카테고리
     * @throws FileSizeExceededException 파일 크기가 제한을 초과한 경우
     */
    void validateSize(MultipartFile file, FileCategory category)
            throws FileSizeExceededException;
}
