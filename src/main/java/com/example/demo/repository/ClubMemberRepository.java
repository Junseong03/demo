package com.example.demo.repository;

import com.example.demo.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    List<ClubMember> findByUserId(Long userId);

    List<ClubMember> findByClubId(Long clubId);
    
    org.springframework.data.domain.Page<ClubMember> findByClubId(Long clubId, org.springframework.data.domain.Pageable pageable);

    Optional<ClubMember> findByUserIdAndClubId(Long userId, Long clubId);

    boolean existsByUserIdAndClubId(Long userId, Long clubId);

    Optional<ClubMember> findByClubIdAndRole(Long clubId, ClubMember.MemberRole role);
}

