package com.tia.lms_backend.exception;

public class S3Exception extends RuntimeException {
    public S3Exception(String errorUploadingImage) {
        super(errorUploadingImage);
    }
    public S3Exception(String errorUploadingImage, Exception e) {
        super(errorUploadingImage,e);
    }
}
