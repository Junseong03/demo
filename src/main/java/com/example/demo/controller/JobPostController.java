package com.example.demo.controller;

import com.example.demo.dto.JobPostDetailDto;
import com.example.demo.dto.JobPostDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostController {
    private final JobPostService jobPostService;

    @GetMapping
    public ResponseEntity<PageResponse<JobPostDto>> getJobPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<JobPostDto> response = jobPostService.getJobPostsWithPagination(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobPostDetailDto> getJobPostDetail(@PathVariable Long jobId) {
        JobPostDetailDto job = jobPostService.getJobPostDetail(jobId);
        return ResponseEntity.ok(job);
    }
}

