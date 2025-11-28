package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    @NotBlank(message = "메시지는 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    private String message;
}

