package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatRoom;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ClubMemberRepository clubMemberRepository;

    // 채팅방 생성/조회 (회장이나 부원은 채팅방을 생성할 수 없음)
    @Transactional
    public ChatRoomDto getOrCreateChatRoom(Long clubId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("동아리를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장이나 이미 가입된 부원은 채팅방을 생성할 수 없음
        if (clubMemberRepository.existsByUserIdAndClubId(userId, clubId)) {
            throw new IllegalArgumentException("동아리 회장이나 부원은 채팅방을 생성할 수 없습니다.");
        }

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

    // 채팅 메시지 조회 (회장 또는 채팅방의 일반 사용자만 조회 가능)
    public PageResponse<ChatMessageDto> getMessages(Long chatRoomId, Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("채팅방을 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(chatRoom.getClub().getId(), ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        boolean isPresident = president.getUser().getId().equals(userId);
        boolean isChatRoomUser = chatRoom.getUser().getId().equals(userId);

        // 회장이거나 채팅방의 일반 사용자만 메시지 조회 가능
        if (!isPresident && !isChatRoomUser) {
            throw new IllegalArgumentException("회장이나 채팅방의 사용자만 메시지를 조회할 수 있습니다.");
        }

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

    // 채팅 메시지 전송 (회장 또는 채팅방의 일반 사용자만 전송 가능)
    @Transactional
    public ChatMessageDto sendMessage(Long chatRoomId, Long userId, SendMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("채팅방을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장 권한 확인
        ClubMember president = clubMemberRepository.findByClubIdAndRole(chatRoom.getClub().getId(), ClubMember.MemberRole.PRESIDENT)
                .orElseThrow(() -> new ResourceNotFoundException("동아리 회장을 찾을 수 없습니다."));

        boolean isPresident = president.getUser().getId().equals(userId);
        boolean isChatRoomUser = chatRoom.getUser().getId().equals(userId);

        // 회장이거나 채팅방의 일반 사용자만 메시지 전송 가능
        if (!isPresident && !isChatRoomUser) {
            throw new IllegalArgumentException("회장이나 채팅방의 사용자만 메시지를 전송할 수 있습니다.");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .message(request.getMessage())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMessageDto.from(savedMessage);
    }

    // 회장용 채팅방 목록 조회
    public PageResponse<ChatRoomListDto> getChatRoomsForPresident(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 회장이 회장인 모든 동아리의 채팅방 조회
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByPresidentUserId(userId, pageable);

        // 각 채팅방의 마지막 메시지와 읽지 않은 메시지 개수 조회
        List<ChatRoomListDto> content = chatRoomPage.getContent().stream()
                .map(chatRoom -> {
                    // 마지막 메시지 조회
                    String lastMessage = null;
                    LocalDateTime lastMessageTime = null;
                    List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByChatRoomId(
                            chatRoom.getId(), PageRequest.of(0, 1));
                    if (!lastMessages.isEmpty()) {
                        ChatMessage lastMsg = lastMessages.get(0);
                        lastMessage = lastMsg.getMessage();
                        lastMessageTime = lastMsg.getTimestamp();
                    }

                    // 읽지 않은 메시지 개수 (현재는 전체 메시지 개수로 반환)
                    Long unreadCount = chatMessageRepository.countByChatRoomId(chatRoom.getId());

                    return ChatRoomListDto.from(chatRoom, lastMessage, lastMessageTime, unreadCount.intValue());
                })
                .collect(Collectors.toList());

        return PageResponse.<ChatRoomListDto>builder()
                .content(content)
                .page(chatRoomPage.getNumber())
                .size(chatRoomPage.getSize())
                .totalElements(chatRoomPage.getTotalElements())
                .build();
    }
}

