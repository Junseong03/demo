package com.example.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityRequest {
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    @Size(max = 2000, message = "내용은 2000자 이하여야 합니다.")
    private String content;

    private List<String> tags;
}

