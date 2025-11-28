package com.example.demo.dto;

import com.example.demo.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime timestamp;

    public static ChatMessageDto from(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoom().getId())
                .senderId(chatMessage.getSender().getId())
                .senderName(chatMessage.getSender().getName())
                .message(chatMessage.getMessage())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }
}

