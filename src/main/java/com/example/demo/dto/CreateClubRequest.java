package com.example.demo.dto;

import com.example.demo.entity.Club;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClubRequest {
    @NotBlank(message = "동아리 이름은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s]+$", message = "동아리 이름은 영어, 숫자, 한글만 입력 가능합니다.")
    private String name;

    @NotNull(message = "동아리 유형은 필수입니다.")
    private Club.ClubType type;

    @NotBlank(message = "소속/학과는 필수입니다.")
    private String department;

    @NotBlank(message = "간단한 설명은 필수입니다.")
    private String description;

    private String fullDescription; // 선택

    private String imageUrl; // 선택 (URL이므로 특수기호 허용)

    private String snsLink; // 선택 (URL이므로 특수기호 허용)

    private List<String> tags; // 선택 (Service에서 검증)

    private Boolean isRecruiting = false; // 기본값 false
}

