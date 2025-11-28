package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRoleRequest {
    @NotBlank(message = "역할은 필수입니다.")
    private String role; // PRESIDENT, VICE_PRESIDENT, ADMIN, MEMBER
}

