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
@Table(name = "job_posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName; // 회사명

    @Column(nullable = false)
    private String position; // 포지션명

    @Column(length = 1000)
    private String description; // 한 줄 소개

    @Column(columnDefinition = "TEXT")
    private String content; // 상세 내용

    private String location; // 지역

    private LocalDate deadline; // 마감일

    private String link; // 공고 링크

    private String major; // 추천 전공

    @ElementCollection
    @CollectionTable(name = "job_tags", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>(); // 태그
}

