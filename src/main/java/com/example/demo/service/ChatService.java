package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatRoom;
import com.example.demo.entity.Club;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    // 채팅방 생성/조회
    @Transactional
    public ChatRoomDto getOrCreateChatRoom(Long clubId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 채팅방이 있으면 반환
        ChatRoom existingRoom = chatRoomRepository.findByClubIdAndUserId(clubId, userId)
                .orElse(null);

        if (existingRoom != null) {
            return ChatRoomDto.from(existingRoom);
        }

        // 없으면 새로 생성
        ChatRoom newRoom = ChatRoom.builder()
                .club(club)
                .user(user)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        return ChatRoomDto.from(savedRoom);
    }

    // 채팅 메시지 조회
    public PageResponse<ChatMessageDto> getMessages(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("채팅방을 찾을 수 없습니다."));

        Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);

        List<ChatMessageDto> content = messagePage.getContent().stream()
                .map(ChatMessageDto::from)
                .collect(Collectors.toList());

        return PageResponse.<ChatMessageDto>builder()
                .content(content)
                .page(messagePage.getNumber())
                .size(messagePage.getSize())
                .totalElements(messagePage.getTotalElements())
                .build();
    }

    // 채팅 메시지 전송
    @Transactional
    public ChatMessageDto sendMessage(Long chatRoomId, Long userId, SendMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("채팅방을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .message(request.getMessage())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMessageDto.from(savedMessage);
    }
}

