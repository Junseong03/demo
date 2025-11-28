package com.example.demo.service;

import com.example.demo.dto.CalendarEventDto;
import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    private final ActivityRepository activityRepository;

    public List<CalendarEventDto> getCalendarEvents(LocalDate from, LocalDate to) {
        List<Activity> activities = activityRepository.findByDateRange(from, to);

        return activities.stream()
                .map(activity -> CalendarEventDto.builder()
                        .id(activity.getId())
                        .title(activity.getTitle())
                        .type("ACTIVITY")
                        .date(activity.getDeadline() != null ? activity.getDeadline() : activity.getStartDate())
                        .link("/api/activities/" + activity.getId())
                        .build())
                .collect(Collectors.toList());
    }
}

