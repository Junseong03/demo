package com.example.demo.dto;

import com.example.demo.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private String imageUrl;
    private List<String> tags;

    public static ActivityDetailDto from(Activity activity) {
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
                .tags(activity.getTags())
                .build();
    }
}

