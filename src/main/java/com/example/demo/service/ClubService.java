package com.example.demo.service;

import com.example.demo.dto.ClubDetailDto;
import com.example.demo.dto.ClubDto;
import com.example.demo.dto.ClubInquiryRequest;
import com.example.demo.dto.CreateClubRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubInquiry;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ClubInquiryRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        return ClubDetailDto.from(club);
    }

    @Transactional
    public void createInquiry(Long clubId, Long userId, ClubInquiryRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        ClubInquiry inquiry = ClubInquiry.builder()
                .club(club)
                .user(user)
                .message(request.getMessage())
                .build();

        clubInquiryRepository.save(inquiry);
    }

    @Transactional
    public ClubDto createClub(CreateClubRequest request) {
        // 동아리 이름 중복 체크
        if (clubRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 동아리 이름입니다.");
        }

        // 태그 리스트 처리 및 검증 (null이면 빈 리스트로)
        List<String> tags = new ArrayList<>();
        if (request.getTags() != null) {
            for (String tag : request.getTags()) {
                if (tag != null && !tag.matches("^[a-zA-Z0-9가-힣]+$")) {
                    throw new IllegalArgumentException("태그는 영어, 숫자, 한글만 입력 가능합니다: " + tag);
                }
                if (tag != null && !tag.trim().isEmpty()) {
                    tags.add(tag.trim());
                }
            }
        }

        Club club = Club.builder()
                .name(request.getName())
                .type(request.getType())
                .department(request.getDepartment())
                .description(request.getDescription())
                .fullDescription(request.getFullDescription())
                .imageUrl(request.getImageUrl())
                .snsLink(request.getSnsLink())
                .isRecruiting(request.getIsRecruiting() != null ? request.getIsRecruiting() : false)
                .tags(tags)
                .build();

        try {
            Club savedClub = clubRepository.save(club);
            return ClubDto.from(savedClub);
        } catch (DataIntegrityViolationException e) {
            // 동시성 문제로 인한 중복 삽입 시도 시 데이터베이스 제약조건 위반
            // 또는 existsByName 체크와 save 사이에 다른 트랜잭션이 끼어든 경우
            throw new IllegalArgumentException("이미 존재하는 동아리 이름입니다.");
        }
    }
}

