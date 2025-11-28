package com.example.demo.service;

import com.example.demo.dto.ActivityDetailDto;
import com.example.demo.dto.ActivityDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.Activity;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {
    private final ActivityRepository activityRepository;

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
}

