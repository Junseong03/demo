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
        // LazyInitializationException 방지를 위해 리스트를 복사
        List<String> skills = profile.getSkills() != null 
                ? new java.util.ArrayList<>(profile.getSkills()) 
                : new java.util.ArrayList<>();
        List<String> projectTypes = profile.getAvailableProjectTypes() != null 
                ? new java.util.ArrayList<>(profile.getAvailableProjectTypes()) 
                : new java.util.ArrayList<>();
        
        return TalentProfileDto.builder()
                .id(profile.getId())
                .userName(profile.getUser().getName())
                .major(profile.getUser().getMajor())
                .introduction(profile.getIntroduction())
                .skills(skills)
                .currentAffiliation(profile.getCurrentAffiliation())
                .portfolioLink(profile.getPortfolioLink())
                .availableProjectTypes(projectTypes)
                .build();
    }
}

