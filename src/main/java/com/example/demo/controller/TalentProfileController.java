package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.TalentProfileDto;
import com.example.demo.service.TalentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talents")
@RequiredArgsConstructor
public class TalentProfileController {
    private final TalentProfileService talentProfileService;

    @GetMapping
    public ResponseEntity<?> getTalentProfiles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        if (page > 0 || size != 10) {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<TalentProfileDto> response = talentProfileService.getTalentProfilesWithPagination(pageable);
            return ResponseEntity.ok(response);
        }
        
        List<TalentProfileDto> profiles = talentProfileService.getTalentProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{talentId}")
    public ResponseEntity<TalentProfileDto> getTalentProfileDetail(@PathVariable Long talentId) {
        TalentProfileDto profile = talentProfileService.getTalentProfileDetail(talentId);
        return ResponseEntity.ok(profile);
    }
}

