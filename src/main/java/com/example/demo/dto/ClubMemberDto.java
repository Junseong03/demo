package com.example.demo.dto;

import com.example.demo.entity.ClubMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMemberDto {
    private Long userId;
    private String name;
    private String email;
    private String major;
    private String role; // PRESIDENT (ADMIN) 또는 MEMBER
    private LocalDateTime joinedAt;

    public static ClubMemberDto from(ClubMember clubMember) {
        return ClubMemberDto.builder()
                .userId(clubMember.getUser().getId())
                .name(clubMember.getUser().getName())
                .email(clubMember.getUser().getEmail())
                .major(clubMember.getUser().getMajor())
                .role(clubMember.getRole() == ClubMember.MemberRole.ADMIN ? "PRESIDENT" : "MEMBER")
                .joinedAt(clubMember.getJoinedAt())
                .build();
    }
}

