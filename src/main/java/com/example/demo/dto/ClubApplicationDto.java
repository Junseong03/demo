package com.example.demo.dto;

import com.example.demo.entity.ClubApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubApplicationDto {
    private Long id;
    private Long clubId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String major;
    private String message;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectReason;

    public static ClubApplicationDto from(ClubApplication application) {
        return ClubApplicationDto.builder()
                .id(application.getId())
                .clubId(application.getClub().getId())
                .userId(application.getUser().getId())
                .userName(application.getUser().getName())
                .userEmail(application.getUser().getEmail())
                .major(application.getUser().getMajor())
                .message(application.getMessage())
                .status(application.getStatus().name())
                .appliedAt(application.getAppliedAt())
                .approvedAt(application.getApprovedAt())
                .rejectedAt(application.getRejectedAt())
                .rejectReason(application.getRejectReason())
                .build();
    }
}

