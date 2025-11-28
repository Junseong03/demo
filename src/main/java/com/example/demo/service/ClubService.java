package com.example.demo.service;

import com.example.demo.dto.ClubDetailDto;
import com.example.demo.dto.ClubDto;
import com.example.demo.dto.ClubInquiryRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubInquiry;
import com.example.demo.entity.User;
import com.example.demo.repository.ClubInquiryRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.UserRepository;
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
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubInquiryRepository clubInquiryRepository;
    private final UserRepository userRepository;

    public List<ClubDto> getClubs(String type, String tag, String keyword) {
        List<Club> clubs;

        if (keyword != null && !keyword.isEmpty()) {
            clubs = clubRepository.searchByKeyword(keyword);
        } else if (type != null && tag != null) {
            Club.ClubType clubType = Club.ClubType.valueOf(type.toUpperCase());
            clubs = clubRepository.findByTypeAndTag(clubType, tag);
        } else if (type != null) {
            Club.ClubType clubType = Club.ClubType.valueOf(type.toUpperCase());
            clubs = clubRepository.findByType(clubType);
        } else {
            clubs = clubRepository.findAll();
        }

        return clubs.stream()
                .map(ClubDto::from)
                .collect(Collectors.toList());
    }

    public PageResponse<ClubDto> getClubsWithPagination(String type, String tag, String keyword, Pageable pageable) {
        Page<Club> clubPage;

        if (keyword != null && !keyword.isEmpty()) {
            clubPage = clubRepository.searchByKeyword(keyword, pageable);
        } else if (type != null && tag != null) {
            Club.ClubType clubType = Club.ClubType.valueOf(type.toUpperCase());
            clubPage = clubRepository.findByTypeAndTag(clubType, tag, pageable);
        } else if (type != null) {
            Club.ClubType clubType = Club.ClubType.valueOf(type.toUpperCase());
            clubPage = clubRepository.findByType(clubType, pageable);
        } else {
            clubPage = clubRepository.findAll(pageable);
        }

        List<ClubDto> content = clubPage.getContent().stream()
                .map(ClubDto::from)
                .collect(Collectors.toList());

        return PageResponse.<ClubDto>builder()
                .content(content)
                .page(clubPage.getNumber())
                .size(clubPage.getSize())
                .totalElements(clubPage.getTotalElements())
                .build();
    }

    public ClubDetailDto getClubDetail(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));
        return ClubDetailDto.from(club);
    }

    @Transactional
    public void createInquiry(Long clubId, Long userId, ClubInquiryRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ClubInquiry inquiry = ClubInquiry.builder()
                .club(club)
                .user(user)
                .message(request.getMessage())
                .build();

        clubInquiryRepository.save(inquiry);
    }
}

