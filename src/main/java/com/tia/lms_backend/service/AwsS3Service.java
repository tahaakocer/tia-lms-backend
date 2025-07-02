package com.tia.lms_backend.service;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tia.lms_backend.exception.S3Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AwsS3Service {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.access.key}")
    private String accessKey;

    @Value("${aws.s3.secret.key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadProfilePicture(String tckn, MultipartFile file) {
        String fileName = "profile-pictures/" + tckn + "_" + file.getOriginalFilename();
        return saveImageToS3(file, fileName);
    }

    public void deleteProfilePicture(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(region)
                    .build();

            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new S3Exception("Error deleting image from S3");
        }
    }

    private String saveImageToS3(MultipartFile file, String fileName) {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(region)
                    .build();

            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
        } catch (Exception e) {
            throw new S3Exception("Error uploading image to S3");
        }
    }

    public void deleteImage(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);

            // AWS kimlik bilgileri
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(region)
                    .build();
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new S3Exception("Error deleting image");
        }
    }

    // URL'den dosya adını ayıklama
    private String extractFileNameFromUrl(String fileUrl) {
        String prefix = "https://" + bucketName + ".s3." + region + ".amazonaws.com/";
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        } else {
            throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
        }
    }
}
