package com.example.demo.dto;

import com.example.demo.entity.ActivityImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityImageDto {
    private Long id;
    private String url;
    private LocalDateTime uploadedAt;

    public static ActivityImageDto from(ActivityImage activityImage) {
        return ActivityImageDto.builder()
                .id(activityImage.getId())
                .url(activityImage.getImageUrl())
                .uploadedAt(activityImage.getUploadedAt())
                .build();
    }
}

