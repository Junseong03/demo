package com.example.demo.dto;

import com.example.demo.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDto {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String category;
    private String organizer;
    private LocalDate deadline;
    private String link;
    private String imageUrl;
    private List<ActivityImageDto> images; // 활동 사진들
    private List<String> tags;

    public static ActivityDto from(Activity activity) {
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
        
        return ActivityDto.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .type(activity.getType().name())
                .category(activity.getCategory() != null ? activity.getCategory().name() : null)
                .organizer(activity.getOrganizer())
                .deadline(activity.getDeadline())
                .link(activity.getLink())
                .imageUrl(activity.getImageUrl())
                .images(images)
                .tags(tags)
                .build();
    }
}

