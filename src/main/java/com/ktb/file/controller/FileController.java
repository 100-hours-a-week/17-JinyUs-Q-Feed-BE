package com.ktb.file.controller;

import com.ktb.common.dto.ApiResponse;
import com.ktb.file.dto.request.PresignedUrlRequest;
import com.ktb.file.dto.response.FileUploadConfirmResponse;
import com.ktb.file.dto.response.PresignedUrlResponse;
import com.ktb.file.service.S3PresignedUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File API", description = "파일 업로드 관리 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private static final String MESSAGE_PRESIGNED_URL_GENERATED = "presigned_url_generated";
    private static final String MESSAGE_UPLOAD_CONFIRMED = "upload_confirmed";

    private final S3PresignedUrlService s3PresignedUrlService;

    @PostMapping("/presigned-url")
    @Operation(
            summary = "Presigned URL 생성",
            description = "S3 직접 업로드를 위한 Presigned URL을 생성합니다. " +
                    "클라이언트는 이 URL을 사용하여 S3에 파일을 직접 업로드할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (파일 크기 초과, 허용되지 않은 파일 형식 등)",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = s3PresignedUrlService.generatePresignedUrl(request);
        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_PRESIGNED_URL_GENERATED, response)
        );
    }

    @PostMapping("/{fileId}/confirm")
    @Operation(
            summary = "파일 업로드 완료 확인",
            description = "S3에 파일 업로드 완료 후 서버에 알리고 파일 상태를 UPLOADED로 업데이트합니다. " +
                    "S3에서 파일 존재 여부를 확인하고 CDN URL을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업로드 확인 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "파일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "S3에 파일이 없음 (업로드 실패)",
                    content = @Content(schema = @Schema(implementation = com.ktb.common.dto.CommonErrorResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<FileUploadConfirmResponse>> confirmUpload(
            @Parameter(description = "파일 ID", example = "123")
            @PathVariable Long fileId
    ) {
        FileUploadConfirmResponse response = s3PresignedUrlService.confirmUpload(fileId);
        return ResponseEntity.ok(
                new ApiResponse<>(MESSAGE_UPLOAD_CONFIRMED, response)
        );
    }
}
