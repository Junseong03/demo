package com.example.demo.dto;

import com.example.demo.entity.JobPost;
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
public class JobPostDetailDto {
    private Long id;
    private String companyName;
    private String position;
    private String description;
    private String content;
    private String location;
    private LocalDate deadline;
    private String link;
    private List<String> tags;

    public static JobPostDetailDto from(JobPost jobPost) {
        return JobPostDetailDto.builder()
                .id(jobPost.getId())
                .companyName(jobPost.getCompanyName())
                .position(jobPost.getPosition())
                .description(jobPost.getDescription())
                .content(jobPost.getContent())
                .location(jobPost.getLocation())
                .deadline(jobPost.getDeadline())
                .link(jobPost.getLink())
                .tags(jobPost.getTags())
                .build();
    }
}

