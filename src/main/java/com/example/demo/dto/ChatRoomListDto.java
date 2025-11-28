package com.example.demo.dto;

import com.example.demo.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListDto {
    private Long id;
    private Long clubId;
    private String clubName;
    private Long userId;
    private String userName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private LocalDateTime createdAt;

    public static ChatRoomListDto from(ChatRoom chatRoom, String lastMessage, LocalDateTime lastMessageTime, Integer unreadCount) {
        return ChatRoomListDto.builder()
                .id(chatRoom.getId())
                .clubId(chatRoom.getClub().getId())
                .clubName(chatRoom.getClub().getName())
                .userId(chatRoom.getUser().getId())
                .userName(chatRoom.getUser().getName())
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .unreadCount(unreadCount != null ? unreadCount : 0)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}

