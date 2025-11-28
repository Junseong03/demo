package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeRecommendationsResponse {
    private List<ClubDto> recommendedClubs;
    private List<ActivityDto> recommendedInSchoolActivities;
    private List<ActivityDto> recommendedOutSchoolActivities;
    private List<JobPostDto> recommendedJobs;
}

