package com.example.demo.service;

import com.example.demo.dto.ClubNoticeDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.ClubNotice;
import com.example.demo.entity.ClubMember;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ClubNoticeRepository;
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
public class ClubNoticeService {
    private final ClubNoticeRepository clubNoticeRepository;
    private final ClubMemberRepository clubMemberRepository;

    public List<ClubNoticeDto> getMyNotices(Long userId) {
        List<ClubMember> memberships = clubMemberRepository.findByUserId(userId);
        List<Long> clubIds = memberships.stream()
                .map(m -> m.getClub().getId())
                .collect(Collectors.toList());

        if (clubIds.isEmpty()) {
            return List.of();
        }

        return clubNoticeRepository.findByClubIdsOrderByCreatedAtDesc(clubIds).stream()
                .map(ClubNoticeDto::from)
                .collect(Collectors.toList());
    }

    public PageResponse<ClubNoticeDto> getMyNoticesWithPagination(Long userId, Pageable pageable) {
        List<ClubMember> memberships = clubMemberRepository.findByUserId(userId);
        List<Long> clubIds = memberships.stream()
                .map(m -> m.getClub().getId())
                .collect(Collectors.toList());

        if (clubIds.isEmpty()) {
            return PageResponse.<ClubNoticeDto>builder()
                    .content(List.of())
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(0)
                    .build();
        }

        Page<ClubNotice> noticePage = clubNoticeRepository.findByClubIdsOrderByCreatedAtDesc(clubIds, pageable);
        
        List<ClubNoticeDto> content = noticePage.getContent().stream()
                .map(ClubNoticeDto::from)
                .collect(Collectors.toList());

        return PageResponse.<ClubNoticeDto>builder()
                .content(content)
                .page(noticePage.getNumber())
                .size(noticePage.getSize())
                .totalElements(noticePage.getTotalElements())
                .build();
    }

    public List<ClubNoticeDto> getClubNotices(Long clubId) {
        return clubNoticeRepository.findByClubIdOrderByCreatedAtDesc(clubId).stream()
                .map(ClubNoticeDto::from)
                .collect(Collectors.toList());
    }

    public ClubNoticeDto getNoticeDetail(Long noticeId) {
        ClubNotice notice = clubNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지를 찾을 수 없습니다."));
        return ClubNoticeDto.from(notice);
    }
}

