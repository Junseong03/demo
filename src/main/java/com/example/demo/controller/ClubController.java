package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<PageResponse<ClubDto>> getClubs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        // 해커톤용: size 기본값을 100으로 설정하여 사실상 전체 조회 가능
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ClubDto> response = clubService.getClubsWithPagination(type, tag, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDetailDto> getClubDetail(@PathVariable Long clubId) {
        ClubDetailDto club = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(club);
    }

    @PostMapping
    public ResponseEntity<ClubDto> createClub(@Valid @RequestBody CreateClubRequest request) {
        ClubDto createdClub = clubService.createClub(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClub);
    }

    @PostMapping("/{clubId}/inquiries")
    public ResponseEntity<Void> createInquiry(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @Valid @RequestBody ClubInquiryRequest request) {
        clubService.createInquiry(clubId, userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{clubId}/admin")
    public ResponseEntity<ClubAdminDto> getClubAdmin(@PathVariable Long clubId) {
        ClubAdminDto admin = clubService.getClubAdmin(clubId);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/{clubId}/admin")
    public ResponseEntity<ClubAdminDto> updateClubAdmin(
            @PathVariable Long clubId,
            @RequestParam Long userId) {
        ClubAdminDto admin = clubService.updateClubAdmin(clubId, userId);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/{clubId}/image")
    public ResponseEntity<ClubImageResponse> getClubImage(@PathVariable Long clubId) {
        ClubImageResponse response = clubService.getClubImage(clubId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{clubId}/image")
    public ResponseEntity<ClubImageResponse> updateClubImage(
            @PathVariable Long clubId,
            @Valid @RequestBody UpdateClubImageRequest request) {
        ClubImageResponse response = clubService.updateClubImage(clubId, request.getImageUrl());
        return ResponseEntity.ok(response);
    }
}

