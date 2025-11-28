package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 동아리명

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClubType type; // 중앙 / 학과

    private String department; // 소속 (컴공과 등)

    @Column(length = 500)
    private String description; // 한 줄 소개

    @Column(length = 1000)
    private String fullDescription; // 상세 소개

    private String imageUrl; // 대표 이미지

    private String snsLink; // SNS 링크

    private Boolean isRecruiting; // 모집 여부

    @ElementCollection
    @CollectionTable(name = "club_tags", joinColumns = @JoinColumn(name = "club_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>(); // 관심 분야 태그

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClubMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClubNotice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClubInquiry> inquiries = new ArrayList<>();

    public enum ClubType {
        CENTRAL, // 중앙
        DEPARTMENT // 학과
    }

    // 해커톤용 간단한 업데이트 메서드
    public void updateImageUrl(String imageUrl) {
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}

