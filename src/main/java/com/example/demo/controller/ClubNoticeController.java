package com.example.demo.controller;

import com.example.demo.dto.ClubNoticeDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.service.ClubNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClubNoticeController {
    private final ClubNoticeService clubNoticeService;

    @GetMapping("/me/notices")
    public ResponseEntity<?> getMyNotices(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        if (page > 0 || size != 10) {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<ClubNoticeDto> response = clubNoticeService.getMyNoticesWithPagination(userId, pageable);
            return ResponseEntity.ok(response);
        }
        
        List<ClubNoticeDto> notices = clubNoticeService.getMyNotices(userId);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/clubs/{clubId}/notices")
    public ResponseEntity<List<ClubNoticeDto>> getClubNotices(@PathVariable Long clubId) {
        List<ClubNoticeDto> notices = clubNoticeService.getClubNotices(clubId);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<ClubNoticeDto> getNoticeDetail(@PathVariable Long noticeId) {
        ClubNoticeDto notice = clubNoticeService.getNoticeDetail(noticeId);
        return ResponseEntity.ok(notice);
    }
}

