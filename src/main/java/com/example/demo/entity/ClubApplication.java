package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "club_applications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String message; // 가입 신청 메시지

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();

    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectReason; // 거절 사유

    public enum ApplicationStatus {
        PENDING,   // 대기 중
        APPROVED,  // 승인됨
        REJECTED   // 거절됨
    }

    // 해커톤용 간단한 업데이트 메서드
    public void approve() {
        this.status = ApplicationStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.rejectReason = reason;
    }
}

