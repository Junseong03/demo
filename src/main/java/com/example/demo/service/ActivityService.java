package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Activity;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {
    private final ActivityRepository activityRepository;
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
        return ActivityDetailDto.from(activity);
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

        // 회장 또는 관리자 권한 확인
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

        if (member.getRole() != ClubMember.MemberRole.PRESIDENT && 
            member.getRole() != ClubMember.MemberRole.ADMIN) {
            throw new IllegalArgumentException("회장이나 관리자만 활동 사진을 업로드할 수 있습니다.");
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

        // 활동 이미지 URL 업데이트
        activity.updateImageUrl(uploadedUrl);
        Activity savedActivity = activityRepository.save(activity);

        return ActivityImageResponse.builder()
                .activityId(savedActivity.getId())
                .imageUrl(savedActivity.getImageUrl())
                .build();
    }
}

