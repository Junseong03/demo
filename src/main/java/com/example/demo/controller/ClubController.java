package com.example.demo.controller;

import com.example.demo.dto.ClubDetailDto;
import com.example.demo.dto.ClubDto;
import com.example.demo.dto.ClubInquiryRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<?> getClubs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        // 페이지네이션 파라미터가 기본값이 아니거나 명시적으로 전달된 경우
        if (page > 0 || size != 10) {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<ClubDto> response = clubService.getClubsWithPagination(type, tag, keyword, pageable);
            return ResponseEntity.ok(response);
        }
        
        // 기본값이면 기존처럼 List 반환 (하위 호환성)
        List<ClubDto> clubs = clubService.getClubs(type, tag, keyword);
        return ResponseEntity.ok(clubs);
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDetailDto> getClubDetail(@PathVariable Long clubId) {
        ClubDetailDto club = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(club);
    }

    @PostMapping("/{clubId}/inquiries")
    public ResponseEntity<Void> createInquiry(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @Valid @RequestBody ClubInquiryRequest request) {
        clubService.createInquiry(clubId, userId, request);
        return ResponseEntity.ok().build();
    }
}

