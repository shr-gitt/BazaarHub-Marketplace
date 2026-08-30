package com.bazaarhub.backend.shared.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final MinioClient minioPublicClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url:http://127.0.0.1:9000}")
    private String minioPublicUrl;

    public MinioService(
            @Qualifier("minioClient") MinioClient minioClient,
            @Qualifier("minioPublicClient") MinioClient minioPublicClient
    ) {
        this.minioClient = minioClient;
        this.minioPublicClient = minioPublicClient;
    }

    public String uploadFile(MultipartFile file) {
        try {
            ensureBucketExistsAndPublic();
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
            throw new RuntimeException("Failed to upload file. " + e.getMessage(), e);
        }
    }

    public String getImageUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
            return fileName;
        }
        String baseUrl = minioPublicUrl != null ? minioPublicUrl.trim().replaceAll("/+$", "") : "http://127.0.0.1:9000";
        return baseUrl + "/" + bucketName + "/" + fileName;
    }

    private void ensureBucketExistsAndPublic() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
            );
        }
        try {
            String policy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Effect": "Allow",
                          "Principal": "*",
                          "Action": ["s3:GetObject"],
                          "Resource": ["arn:aws:s3:::%s/*"]
                        }
                      ]
                    }
                    """.formatted(bucketName);
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build()
            );
        } catch (Exception ignored) {
            // Ignore if policy already set or restricted
        }
    }
}
