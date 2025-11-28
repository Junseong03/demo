package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMembershipDto {
    private Boolean isMember;
    private String role; // PRESIDENT, VICE_PRESIDENT, ADMIN, MEMBER
    private LocalDateTime joinedAt;
}

