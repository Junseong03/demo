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
public class ChatRoomDto {
    private Long id;
    private Long clubId;
    private Long userId;
    private String clubName;
    private LocalDateTime createdAt;

    public static ChatRoomDto from(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .clubId(chatRoom.getClub().getId())
                .userId(chatRoom.getUser().getId())
                .clubName(chatRoom.getClub().getName())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}

