package com.example.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubApplicationRequest {
    @Size(max = 500, message = "가입 신청 메시지는 500자 이하여야 합니다.")
    private String message;
}

