package com.example.demo.repository;

import com.example.demo.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    
    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);
    
    // 채팅방의 마지막 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId ORDER BY cm.timestamp DESC")
    List<ChatMessage> findLastMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);
    
    // 채팅방의 읽지 않은 메시지 개수 (현재는 전체 메시지 개수로 반환, 추후 읽음 처리 기능 추가 시 수정)
    Long countByChatRoomId(Long chatRoomId);
}

