package com.example.demo.controller;

import com.example.demo.dto.ChatRoomListDto;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.service.ChatService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<UserDto> getMyInfo(@RequestParam Long userId) {
        UserDto user = userService.getMyInfo(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateMyInfo(
            @RequestParam Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateMyInfo(userId, request);
        return ResponseEntity.ok(user);
    }

    // 회장용 채팅방 목록 조회 (문의받은 채팅방)
    @GetMapping("/chat-rooms")
    public ResponseEntity<PageResponse<ChatRoomListDto>> getChatRooms(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ChatRoomListDto> response = chatService.getChatRoomsForPresident(userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 일반 사용자용 채팅방 목록 조회 (문의한 채팅방)
    @GetMapping("/chat-rooms/sent")
    public ResponseEntity<PageResponse<ChatRoomListDto>> getSentChatRooms(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ChatRoomListDto> response = chatService.getChatRoomsForUser(userId, pageable);
        return ResponseEntity.ok(response);
    }
}

