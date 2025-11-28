package com.example.demo.repository;

import com.example.demo.entity.ChatRoom;
import com.example.demo.entity.ClubMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByClubIdAndUserId(Long clubId, Long userId);
    
    boolean existsByClubIdAndUserId(Long clubId, Long userId);
    
    // 회장이 회장인 모든 동아리의 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr " +
           "JOIN ClubMember cm ON cr.club.id = cm.club.id " +
           "WHERE cm.user.id = :presidentUserId AND cm.role = 'PRESIDENT' " +
           "ORDER BY cr.createdAt DESC")
    Page<ChatRoom> findByPresidentUserId(@Param("presidentUserId") Long presidentUserId, Pageable pageable);
    
    // 특정 동아리의 모든 채팅방 조회 (회장용)
    List<ChatRoom> findByClubId(Long clubId);
}

