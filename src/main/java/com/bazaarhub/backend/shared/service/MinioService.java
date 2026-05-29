package com.bazaarhub.backend.shared.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadFile(MultipartFile file) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs
                            .builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(bucketName)
                                .build());
            }
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file." + e.getMessage(), e);
        }
    }

    public String getImageUrl(String fileName) {
        try{
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket("bazaarhub")
                            .object(fileName)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
        }
        catch (Exception ex){
            throw new RuntimeException(
                    "Failed to generate presigned URL",
                    ex
            );
        }
}
}
