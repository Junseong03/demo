package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "club_members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    public enum MemberRole {
        MEMBER,         // 일반 멤버
        ADMIN,          // 관리자
        VICE_PRESIDENT, // 부대표
        PRESIDENT       // 회장
    }

    // 해커톤용 간단한 업데이트 메서드
    public void updateRole(MemberRole role) {
        if (role != null) {
            this.role = role;
        }
    }
}

