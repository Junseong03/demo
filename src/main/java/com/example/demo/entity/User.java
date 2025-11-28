package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String major; // 전공

    @ElementCollection
    @CollectionTable(name = "user_interest_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> interestTags = new ArrayList<>(); // 관심 태그

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClubMember> clubMembers = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TalentProfile talentProfile;

    // 해커톤용 간단한 업데이트 메서드
    public void updateInfo(String name, String major, List<String> interestTags) {
        if (name != null) {
            this.name = name;
        }
        if (major != null) {
            this.major = major;
        }
        if (interestTags != null) {
            this.interestTags = interestTags;
        }
    }
}

