package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String major;
    private List<String> interestTags;

    public static UserDto from(User user) {
        // LazyInitializationException 방지를 위해 리스트를 복사
        List<String> tags = user.getInterestTags() != null 
                ? new java.util.ArrayList<>(user.getInterestTags()) 
                : new java.util.ArrayList<>();
        
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .major(user.getMajor())
                .interestTags(tags)
                .build();
    }
}

