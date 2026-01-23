package com.ktb.file.controller;

import com.ktb.common.dto.ApiResponse;
import com.ktb.file.dto.request.PresignedUrlRequest;
import com.ktb.file.dto.response.FileUploadConfirmResponse;
import com.ktb.file.dto.response.PresignedUrlResponse;
import com.ktb.file.service.S3PresignedUrlService;
import com.ktb.swagger.file.FileApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController implements FileApi {

    private static final String MESSAGE_PRESIGNED_URL_GENERATED = "presigned_url_generated";
    private static final String MESSAGE_UPLOAD_CONFIRMED = "upload_confirmed";

    private final S3PresignedUrlService s3PresignedUrlService;

    @PostMapping("/presigned-url")
    @Override
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = s3PresignedUrlService.generatePresignedUrl(request);
        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_PRESIGNED_URL_GENERATED, response)
        );
    }

    @PostMapping("/{fileId}/confirm")
    @Override
    public ResponseEntity<ApiResponse<FileUploadConfirmResponse>> confirmUpload(
            @PathVariable Long fileId
    ) {
        FileUploadConfirmResponse response = s3PresignedUrlService.confirmUpload(fileId);
        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_UPLOAD_CONFIRMED, response)
        );
    }
}
