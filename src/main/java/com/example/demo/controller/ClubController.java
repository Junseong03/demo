package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ActivityService;
import com.example.demo.service.ClubService;
import com.example.demo.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ClubController {
    private final ClubService clubService;
    private final ActivityService activityService;
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
        
        // 디버깅을 위한 로그
        log.debug("파일 업로드 요청 - 파일명: {}, Content-Type: {}, 크기: {} bytes", 
                file.getOriginalFilename(), file.getContentType(), file.getSize());
        
        // 이미지 파일 검증 (Content-Type 또는 파일 확장자로 확인)
        if (!isImageFile(file)) {
            log.warn("이미지 파일이 아님 - 파일명: {}, Content-Type: {}", 
                    file.getOriginalFilename(), file.getContentType());
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다. (지원 형식: JPEG, PNG, GIF, WebP, BMP)");
        }

        // MinIO에 파일 업로드
        String uploadedUrl = fileStorageService.uploadFile(file, "clubs/" + clubId);
        
        // 동아리 이미지 URL 업데이트
        ClubImageResponse response = clubService.uploadClubImage(clubId, uploadedUrl);
        return ResponseEntity.ok(response);
    }
    
    private boolean isImageFile(MultipartFile file) {
        // Content-Type으로 확인
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            log.debug("Content-Type으로 이미지 파일 확인: {}", contentType);
            return true;
        }
        
        // 파일 확장자로 확인 (Content-Type이 없는 경우 대비)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String lowerFilename = originalFilename.toLowerCase();
            boolean isImage = lowerFilename.endsWith(".jpg") || 
                            lowerFilename.endsWith(".jpeg") || 
                            lowerFilename.endsWith(".png") || 
                            lowerFilename.endsWith(".gif") || 
                            lowerFilename.endsWith(".webp") ||
                            lowerFilename.endsWith(".bmp");
            if (isImage) {
                log.debug("파일 확장자로 이미지 파일 확인: {}", originalFilename);
            }
            return isImage;
        }
        
        log.warn("파일명이 null입니다. 이미지 파일 검증 실패");
        return false;
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

    // 동아리 활동 조회
    @GetMapping("/{clubId}/activities")
    public ResponseEntity<PageResponse<ActivityDto>> getClubActivities(
            @PathVariable Long clubId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ActivityDto> response = clubService.getClubActivities(clubId, pageable);
        return ResponseEntity.ok(response);
    }

    // 동아리 부원 목록 조회
    @GetMapping("/{clubId}/members")
    public ResponseEntity<PageResponse<ClubMemberDto>> getClubMembers(
            @PathVariable Long clubId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ClubMemberDto> response = clubService.getClubMembers(clubId, pageable);
        return ResponseEntity.ok(response);
    }

    // 동아리 가입 신청
    @PostMapping("/{clubId}/applications")
    public ResponseEntity<ClubApplicationDto> createApplication(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @Valid @RequestBody ClubApplicationRequest request) {
        ClubApplicationDto application = clubService.createApplication(clubId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    // 동아리 가입 신청 목록 조회 (회장용)
    @GetMapping("/{clubId}/applications")
    public ResponseEntity<PageResponse<ClubApplicationDto>> getApplications(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ClubApplicationDto> response = clubService.getApplications(clubId, status, userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 동아리 가입 신청 승인
    @PutMapping("/{clubId}/applications/{applicationId}/approve")
    public ResponseEntity<ClubApplicationDto> approveApplication(
            @PathVariable Long clubId,
            @PathVariable Long applicationId,
            @RequestParam Long userId) {
        ClubApplicationDto application = clubService.approveApplication(clubId, applicationId, userId);
        return ResponseEntity.ok(application);
    }

    // 동아리 가입 신청 거절
    @PutMapping("/{clubId}/applications/{applicationId}/reject")
    public ResponseEntity<ClubApplicationDto> rejectApplication(
            @PathVariable Long clubId,
            @PathVariable Long applicationId,
            @RequestParam Long userId,
            @RequestBody(required = false) RejectApplicationRequest request) {
        ClubApplicationDto application = clubService.rejectApplication(clubId, applicationId, userId, request);
        return ResponseEntity.ok(application);
    }

    // 현재 사용자의 동아리 멤버십 확인
    @GetMapping("/{clubId}/membership")
    public ResponseEntity<ClubMembershipDto> getMembership(
            @PathVariable Long clubId,
            @RequestParam Long userId) {
        ClubMembershipDto membership = clubService.getMembership(clubId, userId);
        return ResponseEntity.ok(membership);
    }

    // 동아리 정보 수정 (회장 또는 관리자만 가능)
    @PutMapping("/{clubId}")
    public ResponseEntity<ClubDetailDto> updateClub(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdateClubRequest request) {
        ClubDetailDto updatedClub = clubService.updateClub(clubId, userId, request);
        return ResponseEntity.ok(updatedClub);
    }

    // 동아리 부원 권한 변경 (회장만 가능)
    @PutMapping("/{clubId}/members/{memberUserId}/role")
    public ResponseEntity<ClubMemberDto> updateMemberRole(
            @PathVariable Long clubId,
            @PathVariable Long memberUserId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        ClubMemberDto member = clubService.updateMemberRole(clubId, memberUserId, userId, request);
        return ResponseEntity.ok(member);
    }

    // 동아리 부원 탈퇴 처리 (회장만 가능)
    @DeleteMapping("/{clubId}/members/{memberUserId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long clubId,
            @PathVariable Long memberUserId,
            @RequestParam Long userId) {
        clubService.removeMember(clubId, memberUserId, userId);
        return ResponseEntity.ok().build();
    }

    // 동아리 활동 생성 (회장, 부대표, 관리자만 가능)
    @PostMapping("/{clubId}/activities")
    public ResponseEntity<ActivityDetailDto> createActivity(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @Valid @RequestBody CreateActivityRequest request) {
        ActivityDetailDto activity = activityService.createActivity(clubId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    // 동아리 활동 상세 조회 (동아리 컨텍스트)
    @GetMapping("/{clubId}/activities/{activityId}")
    public ResponseEntity<ActivityDetailDto> getClubActivityDetail(
            @PathVariable Long clubId,
            @PathVariable Long activityId) {
        ActivityDetailDto activity = activityService.getClubActivityDetail(clubId, activityId);
        return ResponseEntity.ok(activity);
    }

    // 동아리 활동 수정 (작성자 또는 관리자만 가능)
    @PutMapping("/{clubId}/activities/{activityId}")
    public ResponseEntity<ActivityDetailDto> updateActivity(
            @PathVariable Long clubId,
            @PathVariable Long activityId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdateActivityRequest request) {
        ActivityDetailDto updatedActivity = activityService.updateActivity(clubId, activityId, userId, request);
        return ResponseEntity.ok(updatedActivity);
    }

    // 동아리 활동 삭제 (작성자 또는 관리자만 가능)
    @DeleteMapping("/{clubId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long clubId,
            @PathVariable Long activityId,
            @RequestParam Long userId) {
        activityService.deleteActivity(clubId, activityId, userId);
        return ResponseEntity.ok().build();
    }

    // 동아리 활동 사진 업로드 (회장 또는 관리자만 가능)
    @PostMapping(value = "/{clubId}/activities/{activityId}/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityImageResponse> uploadActivityImage(
            @PathVariable Long clubId,
            @PathVariable Long activityId,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {
        ActivityImageResponse response = activityService.uploadActivityImage(clubId, activityId, userId, file);
        return ResponseEntity.ok(response);
    }

    // 동아리 활동 사진 업데이트 (교체, imageId 기반, 회장 또는 관리자만 가능)
    @PutMapping(value = "/{clubId}/activities/{activityId}/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityImageResponse> updateActivityImage(
            @PathVariable Long clubId,
            @PathVariable Long activityId,
            @PathVariable Long imageId,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {
        ActivityImageResponse response = activityService.updateActivityImage(clubId, activityId, imageId, userId, file);
        return ResponseEntity.ok(response);
    }

    // 동아리 활동 사진 삭제 (imageId 기반, 회장 또는 관리자만 가능)
    @DeleteMapping("/{clubId}/activities/{activityId}/images/{imageId}")
    public ResponseEntity<Void> deleteActivityImage(
            @PathVariable Long clubId,
            @PathVariable Long activityId,
            @PathVariable Long imageId,
            @RequestParam Long userId) {
        activityService.deleteActivityImage(clubId, activityId, imageId, userId);
        return ResponseEntity.ok().build();
    }
}

