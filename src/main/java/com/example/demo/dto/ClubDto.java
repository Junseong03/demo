package com.example.demo.dto;

import com.example.demo.entity.Club;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubDto {
    private Long id;
    private String name;
    private String type;
    private String department;
    private String description;
    private String imageUrl;
    private Boolean isRecruiting;
    private List<String> tags;

    public static ClubDto from(Club club) {
        return ClubDto.builder()
                .id(club.getId())
                .name(club.getName())
                .type(club.getType().name())
                .department(club.getDepartment())
                .description(club.getDescription())
                .imageUrl(club.getImageUrl())
                .isRecruiting(club.getIsRecruiting())
                .tags(club.getTags())
                .build();
    }
}

