package com.ktb.file.domain;

import lombok.Getter;

@Getter
public enum StorageType {
    LOCAL("로컬 파일시스템", "file:///"),
    S3("AWS S3", "s3://"),
    CLOUDFRONT("AWS CloudFront CDN", "https://"),
    GCS("Google Cloud Storage", "gs://");

    private final String displayName;
    private final String urlPrefix;

    StorageType(String displayName, String urlPrefix) {
        this.displayName = displayName;
        this.urlPrefix = urlPrefix;
    }

    public boolean needsUrlPrefix() {
        return this == LOCAL || this == S3 || this == GCS;
    }

    public boolean isCdn() {
        return this == CLOUDFRONT;
    }

    public boolean isCloud() {
        return this == S3 || this == GCS || this == CLOUDFRONT;
    }
}
