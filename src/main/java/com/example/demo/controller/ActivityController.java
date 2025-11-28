package com.example.demo.controller;

import com.example.demo.dto.ActivityDetailDto;
import com.example.demo.dto.ActivityDto;
import com.example.demo.dto.ActivityImageResponse;
import com.example.demo.dto.CreateActivityRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UpdateActivityRequest;
import com.example.demo.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<PageResponse<ActivityDto>> getActivities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ActivityDto> response = activityService.getActivitiesWithPagination(type, category, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDetailDto> getActivityDetail(@PathVariable Long activityId) {
        ActivityDetailDto activity = activityService.getActivityDetail(activityId);
        return ResponseEntity.ok(activity);
    }


}

