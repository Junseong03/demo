package com.example.demo.dto;

import com.example.demo.entity.ClubNotice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubNoticeDto {
    private Long id;
    private Long clubId;
    private String clubName;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public static ClubNoticeDto from(ClubNotice notice) {
        return ClubNoticeDto.builder()
                .id(notice.getId())
                .clubId(notice.getClub().getId())
                .clubName(notice.getClub().getName())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}

