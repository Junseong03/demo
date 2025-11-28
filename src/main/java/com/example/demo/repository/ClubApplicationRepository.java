package com.example.demo.repository;

import com.example.demo.entity.ClubApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Long> {
    List<ClubApplication> findByClubId(Long clubId);
    Page<ClubApplication> findByClubId(Long clubId, Pageable pageable);
    
    Page<ClubApplication> findByClubIdAndStatus(Long clubId, ClubApplication.ApplicationStatus status, Pageable pageable);
    
    Optional<ClubApplication> findByClubIdAndUserId(Long clubId, Long userId);
    
    boolean existsByClubIdAndUserIdAndStatus(Long clubId, Long userId, ClubApplication.ApplicationStatus status);
    
    boolean existsByClubIdAndUserId(Long clubId, Long userId);
}

