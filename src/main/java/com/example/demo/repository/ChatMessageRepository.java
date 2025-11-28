package com.example.demo.repository;

import com.example.demo.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    
    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);
}

