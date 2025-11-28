package com.example.demo.dto;

import com.example.demo.entity.ClubMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubAdminDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private Long clubId;
    private String clubName;

    public static ClubAdminDto from(ClubMember clubMember) {
        return ClubAdminDto.builder()
                .userId(clubMember.getUser().getId())
                .userName(clubMember.getUser().getName())
                .userEmail(clubMember.getUser().getEmail())
                .clubId(clubMember.getClub().getId())
                .clubName(clubMember.getClub().getName())
                .build();
    }
}

