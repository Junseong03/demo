package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ClubService;
import com.example.demo.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<PageResponse<ClubDto>> getClubs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        // 해커톤용: size 기본값을 100으로 설정하여 사실상 전체 조회 가능
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ClubDto> response = clubService.getClubsWithPagination(type, tag, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDetailDto> getClubDetail(@PathVariable Long clubId) {
        ClubDetailDto club = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(club);
    }

    @PostMapping
    public ResponseEntity<ClubDto> createClub(@Valid @RequestBody CreateClubRequest request) {
        ClubDto createdClub = clubService.createClub(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClub);
    }

    @PostMapping("/{clubId}/inquiries")
    public ResponseEntity<Void> createInquiry(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @Valid @RequestBody ClubInquiryRequest request) {
        clubService.createInquiry(clubId, userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{clubId}/admin")
    public ResponseEntity<ClubAdminDto> getClubAdmin(@PathVariable Long clubId) {
        ClubAdminDto admin = clubService.getClubAdmin(clubId);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/{clubId}/admin")
    public ResponseEntity<ClubAdminDto> updateClubAdmin(
            @PathVariable Long clubId,
            @RequestParam Long userId) {
        ClubAdminDto admin = clubService.updateClubAdmin(clubId, userId);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/{clubId}/image")
    public ResponseEntity<ClubImageResponse> getClubImage(@PathVariable Long clubId) {
        ClubImageResponse response = clubService.getClubImage(clubId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{clubId}/image")
    public ResponseEntity<ClubImageResponse> updateClubImage(
            @PathVariable Long clubId,
            @Valid @RequestBody UpdateClubImageRequest request) {
        ClubImageResponse response = clubService.updateClubImage(clubId, request.getImageUrl());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{clubId}/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClubImageResponse> uploadClubImage(
            @PathVariable Long clubId,
            @RequestParam("file") MultipartFile file) {
        // 파일 유효성 검사
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // MinIO에 파일 업로드
        String uploadedUrl = fileStorageService.uploadFile(file, "clubs/" + clubId);
        
        // 동아리 이미지 URL 업데이트
        ClubImageResponse response = clubService.uploadClubImage(clubId, uploadedUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clubId}/image/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadClubImage(
            @PathVariable Long clubId) {
        ClubImageResponse imageInfo = clubService.getClubImage(clubId);
        
        if (imageInfo.getImageUrl() == null || imageInfo.getImageUrl().isEmpty()) {
            throw new com.example.demo.exception.ResourceNotFoundException("동아리 이미지가 없습니다.");
        }

        // MinIO에서 파일 다운로드
        String fileName = fileStorageService.extractFileNameFromUrl(imageInfo.getImageUrl());
        InputStream inputStream = fileStorageService.downloadFile(fileName);
        
        org.springframework.core.io.Resource resource = new org.springframework.core.io.InputStreamResource(inputStream);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @DeleteMapping("/{clubId}/image")
    public ResponseEntity<Void> deleteClubImage(@PathVariable Long clubId) {
        ClubImageResponse imageInfo = clubService.getClubImage(clubId);
        
        // MinIO에서 파일 삭제 (있는 경우)
        if (imageInfo.getImageUrl() != null && !imageInfo.getImageUrl().isEmpty()) {
            String fileName = fileStorageService.extractFileNameFromUrl(imageInfo.getImageUrl());
            fileStorageService.deleteFile(fileName);
        }
        
        // DB에서 이미지 URL 삭제
        clubService.deleteClubImage(clubId);
        return ResponseEntity.ok().build();
    }
}

