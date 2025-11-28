package com.example.demo.dto;

import com.example.demo.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDetailDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String type;
    private String category;
    private String organizer;
    private LocalDate deadline;
    private LocalDate startDate;
    private String link;
    private String imageUrl; // 레거시 호환용
    private List<ActivityImageDto> images; // 활동 사진들
    private List<String> tags;
    private LocalDateTime createdAt;
    private CreatedByDto createdBy;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatedByDto {
        private Long userId;
        private String name;
    }

    public static ActivityDetailDto from(Activity activity) {
        // LazyInitializationException 방지를 위해 리스트를 복사
        List<String> tags = activity.getTags() != null 
                ? new ArrayList<>(activity.getTags()) 
                : new ArrayList<>();
        
        // ActivityImage 리스트를 DTO로 변환
        List<ActivityImageDto> images = new ArrayList<>();
        if (activity.getImages() != null) {
            images = activity.getImages().stream()
                    .map(ActivityImageDto::from)
                    .collect(Collectors.toList());
        }
        
        // 작성자 정보
        CreatedByDto createdBy = null;
        if (activity.getCreatedBy() != null) {
            createdBy = CreatedByDto.builder()
                    .userId(activity.getCreatedBy().getId())
                    .name(activity.getCreatedBy().getName())
                    .build();
        }
        
        return ActivityDetailDto.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .content(activity.getContent())
                .type(activity.getType().name())
                .category(activity.getCategory() != null ? activity.getCategory().name() : null)
                .organizer(activity.getOrganizer())
                .deadline(activity.getDeadline())
                .startDate(activity.getStartDate())
                .link(activity.getLink())
                .imageUrl(activity.getImageUrl())
                .images(images)
                .tags(tags)
                .createdAt(activity.getCreatedAt())
                .createdBy(createdBy)
                .build();
    }
}

