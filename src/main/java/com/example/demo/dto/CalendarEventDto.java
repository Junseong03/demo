package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventDto {
    private Long id;
    private String title;
    private String type; // ACTIVITY, CLUB_NOTICE 등
    private LocalDate date;
    private String link;
}

