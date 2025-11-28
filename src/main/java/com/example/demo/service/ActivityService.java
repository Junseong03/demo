package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityImage;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ActivityImageRepository;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityImageRepository activityImageRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public List<ActivityDto> getActivities(String type, String category) {
        List<Activity> activities;

        if (type != null && category != null) {
            Activity.ActivityType activityType = Activity.ActivityType.valueOf(type.toUpperCase());
            Activity.ActivityCategory activityCategory = Activity.ActivityCategory.valueOf(category.toUpperCase());
            activities = activityRepository.findByTypeAndCategory(activityType, activityCategory);
        } else if (type != null) {
            Activity.ActivityType activityType = Activity.ActivityType.valueOf(type.toUpperCase());
            activities = activityRepository.findByType(activityType);
        } else {
            activities = activityRepository.findAll();
        }

        return activities.stream()
                .map(ActivityDto::from)
                .collect(Collectors.toList());
    }

    public PageResponse<ActivityDto> getActivitiesWithPagination(String type, String category, String keyword, Pageable pageable) {
        Page<Activity> activityPage;

        if (keyword != null && !keyword.isEmpty()) {
            if (type != null && category != null) {
                Activity.ActivityType activityType = Activity.ActivityType.valueOf(type.toUpperCase());
                Activity.ActivityCategory activityCategory = Activity.ActivityCategory.valueOf(category.toUpperCase());
                activityPage = activityRepository.findByTypeAndCategoryAndKeyword(activityType, activityCategory, keyword, pageable);
            } else if (type != null) {
                Activity.ActivityType activityType = Activity.ActivityType.valueOf(type.toUpperCase());
                activityPage = activityRepository.findByTypeAndKeyword(activityType, keyword, pageable);
            } else {
                activityPage = activityRepository.searchByKeyword(keyword, pageable);
            }
        } else if (type != null && category != null) {
            Activity.ActivityType activityType = Activity.ActivityType.valueOf(type.toUpperCase());
            Activity.ActivityCategory activityCategory = Activity.ActivityCategory.valueOf(category.toUpperCase());
            activityPage = activityRepository.findByTypeAndCategory(activityType, activityCategory, pageable);
        } else if (type != null) {
            Activity.ActivityType activityType = Activity.ActivityType.valueOf(type.toUpperCase());
            activityPage = activityRepository.findByType(activityType, pageable);
        } else {
            activityPage = activityRepository.findAll(pageable);
        }

        List<ActivityDto> content = activityPage.getContent().stream()
                .map(ActivityDto::from)
                .collect(Collectors.toList());

        return PageResponse.<ActivityDto>builder()
                .content(content)
                .page(activityPage.getNumber())
                .size(activityPage.getSize())
                .totalElements(activityPage.getTotalElements())
                .build();
    }

    public ActivityDetailDto getActivityDetail(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));
        
        // LazyInitializationException 방지를 위해 images를 명시적으로 초기화
        if (activity.getImages() != null) {
            activity.getImages().size(); // Lazy 로딩 트리거
        }
        
        return ActivityDetailDto.from(activity);
    }

    // 동아리 활동 상세 조회 (동아리 컨텍스트)
    public ActivityDetailDto getClubActivityDetail(Long clubId, Long activityId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));

        // 활동이 해당 동아리에 속하는지 확인
        if (activity.getClub() == null || !activity.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 활동이 아닙니다.");
        }

        // LazyInitializationException 방지를 위해 images를 명시적으로 초기화
        if (activity.getImages() != null) {
            activity.getImages().size(); // Lazy 로딩 트리거
        }

        return ActivityDetailDto.from(activity);
    }

    // 동아리 활동 생성 (회장, 부대표, 관리자만 가능)
    @Transactional
    public ActivityDetailDto createActivity(Long clubId, Long userId, CreateActivityRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장, 부대표, 관리자 권한 확인
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

        if (member.getRole() != ClubMember.MemberRole.PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.VICE_PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.ADMIN) {
            throw new IllegalArgumentException("회장이나 관리자만 활동을 생성할 수 있습니다.");
        }

        // 태그 검증 및 처리
        List<String> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tag : request.getTags()) {
                if (tag != null && !tag.trim().isEmpty()) {
                    tags.add(tag.trim());
                }
            }
        }

        // 활동 생성
        Activity activity = Activity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .type(Activity.ActivityType.IN_SCHOOL) // 동아리 활동은 교내 활동
                .club(club)
                .createdBy(user)
                .tags(tags.isEmpty() ? new ArrayList<>() : tags)
                .build();

        Activity savedActivity = activityRepository.save(activity);
        
        // LazyInitializationException 방지를 위해 images를 명시적으로 초기화
        if (savedActivity.getImages() != null) {
            savedActivity.getImages().size(); // Lazy 로딩 트리거
        }
        
        return ActivityDetailDto.from(savedActivity);
    }

    // 동아리 활동 사진 업로드 (회장 또는 관리자만 가능)
    @Transactional
    public ActivityImageResponse uploadActivityImage(Long clubId, Long activityId, Long userId, MultipartFile file) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장, 부회장, 관리자 권한 확인
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

        if (member.getRole() != ClubMember.MemberRole.PRESIDENT && 
            member.getRole() != ClubMember.MemberRole.VICE_PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.ADMIN) {
            throw new IllegalArgumentException("회장, 부회장, 관리자만 활동 사진을 업로드할 수 있습니다.");
        }

        // 파일 유효성 검사
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 이미지 파일 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String lowerFilename = originalFilename.toLowerCase();
                if (!lowerFilename.endsWith(".jpg") && !lowerFilename.endsWith(".jpeg") &&
                    !lowerFilename.endsWith(".png") && !lowerFilename.endsWith(".gif") &&
                    !lowerFilename.endsWith(".webp") && !lowerFilename.endsWith(".bmp")) {
                    throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
                }
            } else {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }
        }

        // MinIO에 파일 업로드
        String folder = "clubs/" + clubId + "/activities/" + activityId;
        String uploadedUrl = fileStorageService.uploadFile(file, folder);

        // ActivityImage 엔티티 생성 및 저장
        ActivityImage activityImage = ActivityImage.builder()
                .activity(activity)
                .imageUrl(uploadedUrl)
                .build();
        activityImageRepository.save(activityImage);

        return ActivityImageResponse.builder()
                .activityId(activity.getId())
                .imageUrl(uploadedUrl)
                .build();
    }

    // 동아리 활동 수정 (작성자 또는 관리자만 가능)
    @Transactional
    public ActivityDetailDto updateActivity(Long clubId, Long activityId, Long userId, UpdateActivityRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 활동이 해당 동아리에 속하는지 확인
        if (activity.getClub() == null || !activity.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 활동이 아닙니다.");
        }

        // 작성자 또는 관리자 권한 확인
        boolean isAuthor = activity.getCreatedBy() != null && activity.getCreatedBy().getId().equals(userId);
        boolean isAdmin = false;

        if (!isAuthor) {
            ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                    .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

            if (member.getRole() == ClubMember.MemberRole.PRESIDENT ||
                member.getRole() == ClubMember.MemberRole.VICE_PRESIDENT ||
                member.getRole() == ClubMember.MemberRole.ADMIN) {
                isAdmin = true;
            }
        }

        if (!isAuthor && !isAdmin) {
            throw new IllegalArgumentException("작성자나 관리자만 활동을 수정할 수 있습니다.");
        }

        // 태그 검증 및 처리
        List<String> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tag : request.getTags()) {
                if (tag != null && !tag.trim().isEmpty()) {
                    tags.add(tag.trim());
                }
            }
        }

        // 활동 정보 업데이트
        activity.updateInfo(
                request.getTitle(),
                request.getDescription(),
                request.getContent(),
                tags.isEmpty() ? null : tags
        );

        Activity savedActivity = activityRepository.save(activity);
        
        // LazyInitializationException 방지를 위해 images를 명시적으로 초기화
        if (savedActivity.getImages() != null) {
            savedActivity.getImages().size(); // Lazy 로딩 트리거
        }
        
        return ActivityDetailDto.from(savedActivity);
    }

    // 동아리 활동 삭제 (작성자 또는 관리자만 가능)
    @Transactional
    public void deleteActivity(Long clubId, Long activityId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 활동이 해당 동아리에 속하는지 확인
        if (activity.getClub() == null || !activity.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 활동이 아닙니다.");
        }

        // 작성자 또는 관리자 권한 확인
        boolean isAuthor = activity.getCreatedBy() != null && activity.getCreatedBy().getId().equals(userId);
        boolean isAdmin = false;

        if (!isAuthor) {
            ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                    .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

            if (member.getRole() == ClubMember.MemberRole.PRESIDENT ||
                member.getRole() == ClubMember.MemberRole.VICE_PRESIDENT ||
                member.getRole() == ClubMember.MemberRole.ADMIN) {
                isAdmin = true;
            }
        }

        if (!isAuthor && !isAdmin) {
            throw new IllegalArgumentException("작성자나 관리자만 활동을 삭제할 수 있습니다.");
        }

        // 활동의 모든 이미지 삭제 (MinIO에서)
        List<ActivityImage> images = activityImageRepository.findByActivityId(activityId);
        for (ActivityImage image : images) {
            String fileName = fileStorageService.extractFileNameFromUrl(image.getImageUrl());
            if (fileName != null) {
                try {
                    fileStorageService.deleteFile(fileName);
                } catch (Exception e) {
                    // MinIO 삭제 실패해도 계속 진행
                }
            }
        }

        // 활동 삭제 (이미지도 cascade로 함께 삭제됨)
        activityRepository.delete(activity);
    }

    // 동아리 활동 사진 삭제 (imageId 기반)
    @Transactional
    public void deleteActivityImage(Long clubId, Long activityId, Long imageId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 활동이 해당 동아리에 속하는지 확인
        if (activity.getClub() == null || !activity.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 활동이 아닙니다.");
        }

        // 회장, 부회장, 관리자 권한 확인
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

        if (member.getRole() != ClubMember.MemberRole.PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.VICE_PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.ADMIN) {
            throw new IllegalArgumentException("회장, 부회장, 관리자만 활동 사진을 삭제할 수 있습니다.");
        }

        // 이미지 찾기
        ActivityImage activityImage = activityImageRepository.findByIdAndActivityId(imageId, activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동 사진을 찾을 수 없습니다."));

        // MinIO에서 파일 삭제
        String fileName = fileStorageService.extractFileNameFromUrl(activityImage.getImageUrl());
        if (fileName != null) {
            try {
                fileStorageService.deleteFile(fileName);
            } catch (Exception e) {
                // MinIO 삭제 실패해도 DB에서 삭제는 진행
            }
        }

        // DB에서 이미지 삭제
        activityImageRepository.delete(activityImage);
    }

    // 동아리 활동 사진 업데이트 (교체, imageId 기반)
    @Transactional
    public ActivityImageResponse updateActivityImage(Long clubId, Long activityId, Long imageId, Long userId, MultipartFile file) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 활동이 해당 동아리에 속하는지 확인
        if (activity.getClub() == null || !activity.getClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("해당 동아리의 활동이 아닙니다.");
        }

        // 회장, 부회장, 관리자 권한 확인
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

        if (member.getRole() != ClubMember.MemberRole.PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.VICE_PRESIDENT &&
            member.getRole() != ClubMember.MemberRole.ADMIN) {
            throw new IllegalArgumentException("회장, 부회장, 관리자만 활동 사진을 업데이트할 수 있습니다.");
        }

        // 이미지 찾기
        ActivityImage activityImage = activityImageRepository.findByIdAndActivityId(imageId, activityId)
                .orElseThrow(() -> new ResourceNotFoundException("활동 사진을 찾을 수 없습니다."));

        // 파일 유효성 검사
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 이미지 파일 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String lowerFilename = originalFilename.toLowerCase();
                if (!lowerFilename.endsWith(".jpg") && !lowerFilename.endsWith(".jpeg") &&
                    !lowerFilename.endsWith(".png") && !lowerFilename.endsWith(".gif") &&
                    !lowerFilename.endsWith(".webp") && !lowerFilename.endsWith(".bmp")) {
                    throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
                }
            } else {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }
        }

        // 기존 MinIO 파일 삭제
        String oldFileName = fileStorageService.extractFileNameFromUrl(activityImage.getImageUrl());
        if (oldFileName != null) {
            try {
                fileStorageService.deleteFile(oldFileName);
            } catch (Exception e) {
                // MinIO 삭제 실패해도 계속 진행
            }
        }

        // 새 파일을 MinIO에 업로드
        String folder = "clubs/" + clubId + "/activities/" + activityId;
        String uploadedUrl = fileStorageService.uploadFile(file, folder);

        // DB의 이미지 URL 업데이트
        activityImage.updateImageUrl(uploadedUrl);
        activityImageRepository.save(activityImage);

        return ActivityImageResponse.builder()
                .activityId(activity.getId())
                .imageUrl(uploadedUrl)
                .build();
    }
}

