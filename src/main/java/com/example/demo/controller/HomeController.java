package com.example.demo.controller;

import com.example.demo.dto.HomeRecommendationsResponse;
import com.example.demo.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/recommendations")
    public ResponseEntity<HomeRecommendationsResponse> getRecommendations(
            @RequestParam(required = false) Long userId) {
        HomeRecommendationsResponse response = homeService.getRecommendations(userId);
        return ResponseEntity.ok(response);
    }
}

