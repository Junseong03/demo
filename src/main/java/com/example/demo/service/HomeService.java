package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {
    private final ClubRepository clubRepository;
    private final ActivityRepository activityRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    public HomeRecommendationsResponse getRecommendations(Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        List<ClubDto> recommendedClubs = getRecommendedClubs(user);
        List<ActivityDto> recommendedInSchoolActivities = getRecommendedInSchoolActivities(user);
        List<ActivityDto> recommendedOutSchoolActivities = getRecommendedOutSchoolActivities(user);
        List<JobPostDto> recommendedJobs = getRecommendedJobs(user);

        return HomeRecommendationsResponse.builder()
                .recommendedClubs(recommendedClubs)
                .recommendedInSchoolActivities(recommendedInSchoolActivities)
                .recommendedOutSchoolActivities(recommendedOutSchoolActivities)
                .recommendedJobs(recommendedJobs)
                .build();
    }

    private List<ClubDto> getRecommendedClubs(User user) {
        List<Club> clubs;
        
        if (user != null && !user.getInterestTags().isEmpty()) {
            // 유저 관심 태그 기준으로 필터링
            String tag = user.getInterestTags().get(0);
            clubs = clubRepository.findAll().stream()
                    .filter(club -> club.getTags().contains(tag))
                    .collect(Collectors.toList());
        } else if (user != null && user.getMajor() != null) {
            // 전공 기준으로 필터링 (예: 전공과 관련된 태그)
            clubs = clubRepository.findAll();
        } else {
            // 랜덤 추천
            clubs = clubRepository.findAll();
        }

        Collections.shuffle(clubs);
        return clubs.stream()
                .limit(5)
                .map(ClubDto::from)
                .collect(Collectors.toList());
    }

    private List<ActivityDto> getRecommendedInSchoolActivities(User user) {
        List<Activity> activities = activityRepository.findByType(Activity.ActivityType.IN_SCHOOL);
        
        if (user != null && !user.getInterestTags().isEmpty()) {
            String tag = user.getInterestTags().get(0);
            activities = activities.stream()
                    .filter(activity -> activity.getTags().contains(tag))
                    .collect(Collectors.toList());
        }

        Collections.shuffle(activities);
        return activities.stream()
                .limit(5)
                .map(ActivityDto::from)
                .collect(Collectors.toList());
    }

    private List<ActivityDto> getRecommendedOutSchoolActivities(User user) {
        List<Activity> activities = activityRepository.findByType(Activity.ActivityType.OUT_SCHOOL);
        
        if (user != null && !user.getInterestTags().isEmpty()) {
            String tag = user.getInterestTags().get(0);
            activities = activities.stream()
                    .filter(activity -> activity.getTags().contains(tag))
                    .collect(Collectors.toList());
        }

        Collections.shuffle(activities);
        return activities.stream()
                .limit(5)
                .map(ActivityDto::from)
                .collect(Collectors.toList());
    }

    private List<JobPostDto> getRecommendedJobs(User user) {
        List<JobPost> jobs;
        
        if (user != null && user.getMajor() != null) {
            jobs = jobPostRepository.findByMajor(user.getMajor());
        } else {
            jobs = jobPostRepository.findAll();
        }

        Collections.shuffle(jobs);
        return jobs.stream()
                .limit(5)
                .map(JobPostDto::from)
                .collect(Collectors.toList());
    }
}

