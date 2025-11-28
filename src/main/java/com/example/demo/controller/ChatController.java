package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 채팅방 생성/조회
    @GetMapping("/clubs/{clubId}/chat-rooms")
    public ResponseEntity<ChatRoomDto> getOrCreateChatRoom(
            @PathVariable Long clubId,
            @RequestParam Long userId) {
        ChatRoomDto chatRoom = chatService.getOrCreateChatRoom(clubId, userId);
        return ResponseEntity.ok(chatRoom);
    }

    // 채팅 메시지 조회 (회장 또는 채팅방의 일반 사용자만 조회 가능)
    @GetMapping("/chat-rooms/{chatRoomId}/messages")
    public ResponseEntity<PageResponse<ChatMessageDto>> getMessages(
            @PathVariable Long chatRoomId,
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ChatMessageDto> response = chatService.getMessages(chatRoomId, userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 채팅 메시지 전송 (회장 또는 채팅방의 일반 사용자만 전송 가능)
    @PostMapping("/chat-rooms/{chatRoomId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long chatRoomId,
            @RequestParam Long userId,
            @Valid @RequestBody SendMessageRequest request) {
        ChatMessageDto message = chatService.sendMessage(chatRoomId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}

