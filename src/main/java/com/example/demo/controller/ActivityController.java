package com.example.demo.controller;

import com.example.demo.dto.ActivityDetailDto;
import com.example.demo.dto.ActivityDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<?> getActivities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        if (page > 0 || size != 10) {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<ActivityDto> response = activityService.getActivitiesWithPagination(type, category, pageable);
            return ResponseEntity.ok(response);
        }
        
        List<ActivityDto> activities = activityService.getActivities(type, category);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDetailDto> getActivityDetail(@PathVariable Long activityId) {
        ActivityDetailDto activity = activityService.getActivityDetail(activityId);
        return ResponseEntity.ok(activity);
    }
}

