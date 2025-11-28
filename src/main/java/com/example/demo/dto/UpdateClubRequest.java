package com.example.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClubRequest {
    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    @Size(max = 1000, message = "상세 설명은 1000자 이하여야 합니다.")
    private String fullDescription;

    private String snsLink;

    private Boolean isRecruiting;

    private List<String> tags;
}

