package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubInquiry;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ClubApplication;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ClubApplicationRepository;
import com.example.demo.repository.ClubInquiryRepository;
import com.example.demo.repository.ClubMemberRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubInquiryRepository clubInquiryRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ClubApplicationRepository clubApplicationRepository;

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

        // 회장이나 이미 가입된 부원은 문의할 수 없음
        if (clubMemberRepository.existsByUserIdAndClubId(userId, clubId)) {
            throw new IllegalArgumentException("동아리 회장이나 부원은 문의할 수 없습니다.");
        }

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

        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

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

            // 동아리 생성자를 회장(PRESIDENT)으로 자동 설정
            ClubMember presidentMember = ClubMember.builder()
                    .user(user)
                    .club(savedClub)
                    .role(ClubMember.MemberRole.PRESIDENT)
                    .build();
            clubMemberRepository.save(presidentMember);

            return ClubDto.from(savedClub);
        } catch (DataIntegrityViolationException e) {
            // 동시성 문제로 인한 중복 삽입 시도 시 데이터베이스 제약조건 위반
            // 또는 existsByName 체크와 save 사이에 다른 트랜잭션이 끼어든 경우
            throw new IllegalArgumentException("이미 존재하는 동아리 이름입니다.");
        }
    }

    public ClubAdminDto getClubAdmin(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        ClubMember president = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        return ClubAdminDto.from(president);
    }

    @Transactional
    public ClubAdminDto updateClubAdmin(Long clubId, Long newAdminUserId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        User newAdmin = userRepository.findById(newAdminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 회장 찾기
        ClubMember existingPresident = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("기존 회장을 찾을 수 없습니다."));

        // 새 회장이 이미 동아리 멤버인지 확인
        ClubMember newPresidentMember = clubMemberRepository.findByUserIdAndClubId(newAdminUserId, clubId)
                .orElse(null);

        // 기존 회장을 관리자(ADMIN)로 변경
        existingPresident.updateRole(ClubMember.MemberRole.ADMIN);
        clubMemberRepository.save(existingPresident);

        // 새 회장 설정
        if (newPresidentMember != null) {
            // 이미 멤버인 경우 역할만 변경
            newPresidentMember.updateRole(ClubMember.MemberRole.PRESIDENT);
            clubMemberRepository.save(newPresidentMember);
        } else {
            // 새로 멤버 추가
            newPresidentMember = ClubMember.builder()
                    .user(newAdmin)
                    .club(club)
                    .role(ClubMember.MemberRole.PRESIDENT)
                    .build();
            clubMemberRepository.save(newPresidentMember);
        }

        return ClubAdminDto.from(newPresidentMember);
    }

    @Transactional
    public ClubImageResponse updateClubImage(Long clubId, String imageUrl) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        club.updateImageUrl(imageUrl);
        Club savedClub = clubRepository.save(club);

        return ClubImageResponse.builder()
                .clubId(savedClub.getId())
                .clubName(savedClub.getName())
                .imageUrl(savedClub.getImageUrl())
                .build();
    }

    @Transactional
    public ClubImageResponse uploadClubImage(Long clubId, String uploadedFileUrl) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        club.updateImageUrl(uploadedFileUrl);
        Club savedClub = clubRepository.save(club);

        return ClubImageResponse.builder()
                .clubId(savedClub.getId())
                .clubName(savedClub.getName())
                .imageUrl(savedClub.getImageUrl())
                .build();
    }

    @Transactional
    public void deleteClubImage(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        club.updateImageUrl(null);
        clubRepository.save(club);
    }

    public ClubImageResponse getClubImage(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        return ClubImageResponse.builder()
                .clubId(club.getId())
                .clubName(club.getName())
                .imageUrl(club.getImageUrl())
                .build();
    }

    // 동아리 활동 조회
    @Transactional(readOnly = true)
    public PageResponse<ActivityDto> getClubActivities(Long clubId, Pageable pageable) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        Page<Activity> activityPage = activityRepository.findByClubId(clubId, pageable);

        // LazyInitializationException 방지를 위해 images를 명시적으로 로드
        List<ActivityDto> content = activityPage.getContent().stream()
                .map(activity -> {
                    // images를 명시적으로 초기화
                    if (activity.getImages() != null) {
                        activity.getImages().size(); // Lazy 로딩 트리거
                    }
                    return ActivityDto.from(activity);
                })
                .collect(Collectors.toList());

        return PageResponse.<ActivityDto>builder()
                .content(content)
                .page(activityPage.getNumber())
                .size(activityPage.getSize())
                .totalElements(activityPage.getTotalElements())
                .build();
    }

    // 동아리 부원 목록 조회 (역할 순서대로 정렬: PRESIDENT > VICE_PRESIDENT > ADMIN > MEMBER)
    public PageResponse<ClubMemberDto> getClubMembers(Long clubId, Pageable pageable) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        // 역할 순서대로 정렬된 부원 목록 조회
        Page<ClubMember> memberPage = clubMemberRepository.findByClubIdOrderByRoleAscJoinedAtAsc(clubId, pageable);

        // 역할 우선순위에 따라 정렬 (PRESIDENT > VICE_PRESIDENT > ADMIN > MEMBER)
        List<ClubMember> sortedMembers = new ArrayList<>(memberPage.getContent());
        sortedMembers.sort((m1, m2) -> {
            int priority1 = getRolePriority(m1.getRole());
            int priority2 = getRolePriority(m2.getRole());
            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }
            return m1.getJoinedAt().compareTo(m2.getJoinedAt());
        });

        List<ClubMemberDto> content = sortedMembers.stream()
                .map(ClubMemberDto::from)
                .collect(Collectors.toList());

        return PageResponse.<ClubMemberDto>builder()
                .content(content)
                .page(memberPage.getNumber())
                .size(memberPage.getSize())
                .totalElements(memberPage.getTotalElements())
                .build();
    }
    
    private int getRolePriority(ClubMember.MemberRole role) {
        switch (role) {
            case PRESIDENT:
                return 1;
            case VICE_PRESIDENT:
                return 2;
            case ADMIN:
                return 3;
            case MEMBER:
            default:
                return 4;
        }
    }

    // 동아리 가입 신청
    @Transactional
    public ClubApplicationDto createApplication(Long clubId, Long userId, ClubApplicationRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장이나 이미 가입된 부원은 가입 신청할 수 없음
        if (clubMemberRepository.existsByUserIdAndClubId(userId, clubId)) {
            throw new IllegalArgumentException("동아리 회장이나 부원은 가입 신청할 수 없습니다.");
        }

        // 이미 대기 중인 신청이 있는지 확인
        if (clubApplicationRepository.existsByClubIdAndUserIdAndStatus(clubId, userId, ClubApplication.ApplicationStatus.PENDING)) {
            throw new IllegalArgumentException("이미 가입 신청이 있습니다.");
        }

        ClubApplication application = ClubApplication.builder()
                .club(club)
                .user(user)
                .message(request.getMessage())
                .status(ClubApplication.ApplicationStatus.PENDING)
                .build();

        ClubApplication savedApplication = clubApplicationRepository.save(application);
        return ClubApplicationDto.from(savedApplication);
    }

    // 동아리 가입 신청 목록 조회 (회장용)
    public PageResponse<ClubApplicationDto> getApplications(Long clubId, String status, Long userId, Pageable pageable) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        if (!president.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장만 가입 신청 목록을 조회할 수 있습니다.");
        }

        Page<ClubApplication> applicationPage;
        if (status != null && !status.isEmpty()) {
            ClubApplication.ApplicationStatus applicationStatus = ClubApplication.ApplicationStatus.valueOf(status.toUpperCase());
            applicationPage = clubApplicationRepository.findByClubIdAndStatus(clubId, applicationStatus, pageable);
        } else {
            applicationPage = clubApplicationRepository.findByClubId(clubId, pageable);
        }

        List<ClubApplicationDto> content = applicationPage.getContent().stream()
                .map(ClubApplicationDto::from)
                .collect(Collectors.toList());

        return PageResponse.<ClubApplicationDto>builder()
                .content(content)
                .page(applicationPage.getNumber())
                .size(applicationPage.getSize())
                .totalElements(applicationPage.getTotalElements())
                .build();
    }

    // 동아리 가입 신청 승인
    @Transactional
    public ClubApplicationDto approveApplication(Long clubId, Long applicationId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        if (!president.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장만 가입 신청을 승인할 수 있습니다.");
        }

        ClubApplication application = clubApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("가입 신청을 찾을 수 없습니다."));

        if (application.getStatus() != ClubApplication.ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신청입니다.");
        }

        // 신청 승인
        application.approve();
        clubApplicationRepository.save(application);

        // 동아리 멤버로 추가
        ClubMember newMember = ClubMember.builder()
                .user(application.getUser())
                .club(club)
                .role(ClubMember.MemberRole.MEMBER)
                .build();
        clubMemberRepository.save(newMember);

        return ClubApplicationDto.from(application);
    }

    // 동아리 가입 신청 거절
    @Transactional
    public ClubApplicationDto rejectApplication(Long clubId, Long applicationId, Long userId, RejectApplicationRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        if (!president.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장만 가입 신청을 거절할 수 있습니다.");
        }

        ClubApplication application = clubApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("가입 신청을 찾을 수 없습니다."));

        if (application.getStatus() != ClubApplication.ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신청입니다.");
        }

        // 신청 거절
        application.reject(request != null ? request.getReason() : null);
        clubApplicationRepository.save(application);

        return ClubApplicationDto.from(application);
    }

    // 현재 사용자의 동아리 멤버십 확인
    public ClubMembershipDto getMembership(Long clubId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        Optional<ClubMember> member = clubMemberRepository.findByUserIdAndClubId(userId, clubId);
        
        if (member.isEmpty()) {
            return ClubMembershipDto.builder()
                    .isMember(false)
                    .role(null)
                    .joinedAt(null)
                    .build();
        }

        ClubMember clubMember = member.get();
        String roleName;
        switch (clubMember.getRole()) {
            case PRESIDENT:
                roleName = "PRESIDENT";
                break;
            case VICE_PRESIDENT:
                roleName = "VICE_PRESIDENT";
                break;
            case ADMIN:
                roleName = "ADMIN";
                break;
            case MEMBER:
            default:
                roleName = "MEMBER";
                break;
        }

        return ClubMembershipDto.builder()
                .isMember(true)
                .role(roleName)
                .joinedAt(clubMember.getJoinedAt())
                .build();
    }

    // 동아리 정보 수정 (회장 또는 관리자만 가능)
    @Transactional
    public ClubDetailDto updateClub(Long clubId, Long userId, UpdateClubRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장 또는 관리자 권한 확인
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리 부원이 아닙니다."));

        if (member.getRole() != ClubMember.MemberRole.PRESIDENT && 
            member.getRole() != ClubMember.MemberRole.ADMIN) {
            throw new IllegalArgumentException("회장이나 관리자만 동아리 정보를 수정할 수 있습니다.");
        }

        // 태그 검증 및 처리
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

        // 동아리 정보 업데이트
        club.updateInfo(
                request.getDescription(),
                request.getFullDescription(),
                request.getSnsLink(),
                request.getIsRecruiting(),
                tags.isEmpty() ? null : tags
        );

        Club savedClub = clubRepository.save(club);
        return ClubDetailDto.from(savedClub);
    }

    // 동아리 부원 권한 변경 (회장만 가능)
    @Transactional
    public ClubMemberDto updateMemberRole(Long clubId, Long memberUserId, Long userId, UpdateMemberRoleRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        if (!president.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장만 부원 권한을 변경할 수 있습니다.");
        }

        // 권한을 변경할 부원 찾기
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(memberUserId, clubId)
                .orElseThrow(() -> new ResourceNotFoundException("부원을 찾을 수 없습니다."));

        // 회장 자신의 권한은 변경 불가
        if (member.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장 권한은 변경할 수 없습니다.");
        }

        // 역할 변환
        ClubMember.MemberRole newRole;
        try {
            newRole = ClubMember.MemberRole.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 역할입니다 (PRESIDENT, VICE_PRESIDENT, ADMIN, MEMBER 중 하나)");
        }

        // 역할 업데이트
        member.updateRole(newRole);
        ClubMember savedMember = clubMemberRepository.save(member);

        return ClubMemberDto.from(savedMember);
    }

    // 동아리 부원 탈퇴 처리 (회장만 가능)
    @Transactional
    public void removeMember(Long clubId, Long memberUserId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(clubId, ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        if (!president.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장만 부원을 탈퇴시킬 수 있습니다.");
        }

        // 탈퇴시킬 부원 찾기
        ClubMember member = clubMemberRepository.findByUserIdAndClubId(memberUserId, clubId)
                .orElseThrow(() -> new ResourceNotFoundException("부원을 찾을 수 없습니다."));

        // 회장 자신은 탈퇴 불가
        if (member.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("회장 자신은 탈퇴시킬 수 없습니다.");
        }

        // 부원 삭제
        clubMemberRepository.delete(member);
    }
}

