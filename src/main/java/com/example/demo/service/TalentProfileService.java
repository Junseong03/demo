package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TalentProfileDto;
import com.example.demo.entity.TalentProfile;
import com.example.demo.repository.TalentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalentProfileService {
    private final TalentProfileRepository talentProfileRepository;

    public List<TalentProfileDto> getTalentProfiles() {
        return talentProfileRepository.findAll().stream()
                .map(TalentProfileDto::from)
                .collect(Collectors.toList());
    }

    public PageResponse<TalentProfileDto> getTalentProfilesWithPagination(Pageable pageable) {
        Page<TalentProfile> profilePage = talentProfileRepository.findAll(pageable);
        
        List<TalentProfileDto> content = profilePage.getContent().stream()
                .map(TalentProfileDto::from)
                .collect(Collectors.toList());

        return PageResponse.<TalentProfileDto>builder()
                .content(content)
                .page(profilePage.getNumber())
                .size(profilePage.getSize())
                .totalElements(profilePage.getTotalElements())
                .build();
    }

    public TalentProfileDto getTalentProfileDetail(Long talentId) {
        TalentProfile profile = talentProfileRepository.findById(talentId)
                .orElseThrow(() -> new RuntimeException("인재 프로필을 찾을 수 없습니다."));
        return TalentProfileDto.from(profile);
    }
}

