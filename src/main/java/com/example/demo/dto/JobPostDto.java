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
public class JobPostDto {
    private Long id;
    private String companyName;
    private String position;
    private String description;
    private String location;
    private LocalDate deadline;
    private String link;
    private List<String> tags;

    public static JobPostDto from(JobPost jobPost) {
        return JobPostDto.builder()
                .id(jobPost.getId())
                .companyName(jobPost.getCompanyName())
                .position(jobPost.getPosition())
                .description(jobPost.getDescription())
                .location(jobPost.getLocation())
                .deadline(jobPost.getDeadline())
                .link(jobPost.getLink())
                .tags(jobPost.getTags())
                .build();
    }
}

