package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClubImageRequest {
    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;
}

