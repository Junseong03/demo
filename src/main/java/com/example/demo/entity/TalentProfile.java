package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "talent_profiles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 1000)
    private String introduction; // 자기소개

    @ElementCollection
    @CollectionTable(name = "talent_skills", joinColumns = @JoinColumn(name = "talent_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>(); // 기술 스택

    private String currentAffiliation; // 현재 소속

    private String portfolioLink; // 포트폴리오 링크

    @ElementCollection
    @CollectionTable(name = "talent_project_types", joinColumns = @JoinColumn(name = "talent_id"))
    @Column(name = "project_type")
    @Builder.Default
    private List<String> availableProjectTypes = new ArrayList<>(); // 참여 가능한 프로젝트 유형
}

