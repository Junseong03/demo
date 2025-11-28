package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activities")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description; // 한 줄 소개

    @Column(columnDefinition = "TEXT")
    private String content; // 상세 내용

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType type; // IN_SCHOOL / OUT_SCHOOL

    @Enumerated(EnumType.STRING)
    private ActivityCategory category; // 공모전 / 대회 / 봉사 / 기타

    private String organizer; // 주최

    private LocalDate deadline; // 마감일

    private LocalDate startDate; // 시작일 (캘린더용)

    private String link; // 관련 링크

    private String imageUrl; // 대표 이미지

    @ElementCollection
    @CollectionTable(name = "activity_tags", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>(); // 태그

    public enum ActivityType {
        IN_SCHOOL, // 교내
        OUT_SCHOOL // 교외
    }

    public enum ActivityCategory {
        CONTEST, // 공모전
        COMPETITION, // 대회
        VOLUNTEER, // 봉사
        OTHER // 기타
    }
}

