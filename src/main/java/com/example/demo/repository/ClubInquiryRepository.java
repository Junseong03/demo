package com.example.demo.repository;

import com.example.demo.entity.ClubInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubInquiryRepository extends JpaRepository<ClubInquiry, Long> {
    List<ClubInquiry> findByClubIdOrderByCreatedAtDesc(Long clubId);
}

