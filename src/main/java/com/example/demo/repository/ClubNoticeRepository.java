package com.example.demo.repository;

import com.example.demo.entity.ClubNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubNoticeRepository extends JpaRepository<ClubNotice, Long> {
    List<ClubNotice> findByClubIdOrderByCreatedAtDesc(Long clubId);
    Page<ClubNotice> findByClubIdOrderByCreatedAtDesc(Long clubId, Pageable pageable);

    @Query("SELECT n FROM ClubNotice n WHERE n.club.id IN :clubIds ORDER BY n.createdAt DESC")
    List<ClubNotice> findByClubIdsOrderByCreatedAtDesc(@Param("clubIds") List<Long> clubIds);
    
    @Query("SELECT n FROM ClubNotice n WHERE n.club.id IN :clubIds ORDER BY n.createdAt DESC")
    Page<ClubNotice> findByClubIdsOrderByCreatedAtDesc(@Param("clubIds") List<Long> clubIds, Pageable pageable);
}

