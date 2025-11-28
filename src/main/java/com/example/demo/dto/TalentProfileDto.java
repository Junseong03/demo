package com.example.demo.dto;

import com.example.demo.entity.TalentProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentProfileDto {
    private Long id;
    private String userName;
    private String major;
    private String introduction;
    private List<String> skills;
    private String currentAffiliation;
    private String portfolioLink;
    private List<String> availableProjectTypes;

    public static TalentProfileDto from(TalentProfile profile) {
        return TalentProfileDto.builder()
                .id(profile.getId())
                .userName(profile.getUser().getName())
                .major(profile.getUser().getMajor())
                .introduction(profile.getIntroduction())
                .skills(profile.getSkills())
                .currentAffiliation(profile.getCurrentAffiliation())
                .portfolioLink(profile.getPortfolioLink())
                .availableProjectTypes(profile.getAvailableProjectTypes())
                .build();
    }
}

