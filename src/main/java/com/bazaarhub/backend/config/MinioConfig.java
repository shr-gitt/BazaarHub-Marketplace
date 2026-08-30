package com.bazaarhub.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String minioEndpoint;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;


    @Bean("minioClient")
    public MinioClient minioClient(){
        return MinioClient
                .builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey,minioSecretKey)
                .build();
    }

    @Bean("minioPublicClient")
    public MinioClient minioPublicClient(
            @Value("${minio.public-url:http://127.0.0.1:9000}") String publicUrl,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(publicUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
