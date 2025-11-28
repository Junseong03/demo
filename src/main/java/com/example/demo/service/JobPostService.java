package com.example.demo.service;

import com.example.demo.dto.JobPostDetailDto;
import com.example.demo.dto.JobPostDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.JobPost;
import com.example.demo.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;

    public List<JobPostDto> getJobPosts() {
        return jobPostRepository.findAll().stream()
                .map(JobPostDto::from)
                .collect(Collectors.toList());
    }

    public PageResponse<JobPostDto> getJobPostsWithPagination(Pageable pageable) {
        Page<JobPost> jobPage = jobPostRepository.findAll(pageable);
        
        List<JobPostDto> content = jobPage.getContent().stream()
                .map(JobPostDto::from)
                .collect(Collectors.toList());

        return PageResponse.<JobPostDto>builder()
                .content(content)
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .build();
    }

    public JobPostDetailDto getJobPostDetail(Long jobId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("채용 공고를 찾을 수 없습니다."));
        return JobPostDetailDto.from(jobPost);
    }
}

