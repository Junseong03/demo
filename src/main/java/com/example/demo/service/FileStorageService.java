package com.example.demo.service;

import com.example.demo.config.MinIOConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    private final MinioClient minioClient;
    private final MinIOConfig minIOConfig;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 버킷이 없으면 생성
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minIOConfig.getBucketName())
                        .build());
                
                // 버킷 정책을 public-read로 설정 (해커톤용 - 외부에서 이미지 접근 가능)
                setBucketPublicReadPolicy();
            }

            // 파일명 생성 (UUID + 원본 파일명)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = folder + "/" + UUID.randomUUID() + extension;

            // 파일 업로드
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 접근 가능한 URL 반환
            return minIOConfig.getEndpoint() + "/" + minIOConfig.getBucketName() + "/" + fileName;
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 다운로드에 실패했습니다: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    public String extractFileNameFromUrl(String url) {
        if (url == null || !url.contains("/")) {
            return null;
        }
        // URL에서 버킷명 이후의 경로 추출
        // 예: http://localhost:9000/club-images/clubs/1/uuid.jpg
        // -> clubs/1/uuid.jpg
        String bucketPrefix = "/" + minIOConfig.getBucketName() + "/";
        int index = url.indexOf(bucketPrefix);
        if (index != -1) {
            return url.substring(index + bucketPrefix.length());
        }
        
        // 버킷명이 없는 경우 마지막 경로만 추출
        String[] parts = url.split("/");
        if (parts.length >= 2) {
            return parts[parts.length - 2] + "/" + parts[parts.length - 1];
        }
        return parts[parts.length - 1];
    }
    
    private void setBucketPublicReadPolicy() {
        try {
            // Public read 정책 JSON (S3 호환)
            String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {
                        "AWS": ["*"]
                      },
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(minIOConfig.getBucketName());
            
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .config(policy)
                    .build());
            
            log.info("MinIO 버킷 정책을 public-read로 설정했습니다: {}", minIOConfig.getBucketName());
        } catch (Exception e) {
            log.warn("MinIO 버킷 정책 설정 실패 (이미지 직접 접근 불가): {}", e.getMessage());
            log.warn("백엔드 API를 통해 이미지를 다운로드하세요: GET /api/clubs/{clubId}/image/download");
        }
    }
}

