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
        String roleName;
        switch (clubMember.getRole()) {
            case PRESIDENT:
                roleName = "PRESIDENT";
                break;
            case VICE_PRESIDENT:
                roleName = "VICE_PRESIDENT";
                break;
            case ADMIN:
                roleName = "ADMIN";
                break;
            case MEMBER:
            default:
                roleName = "MEMBER";
                break;
        }
        
        return ClubMemberDto.builder()
                .userId(clubMember.getUser().getId())
                .name(clubMember.getUser().getName())
                .email(clubMember.getUser().getEmail())
                .major(clubMember.getUser().getMajor())
                .role(roleName)
                .joinedAt(clubMember.getJoinedAt())
                .build();
    }
}

