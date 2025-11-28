package com.example.demo.controller;

import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getMyInfo(@RequestParam Long userId) {
        UserDto user = userService.getMyInfo(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateMyInfo(
            @RequestParam Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateMyInfo(userId, request);
        return ResponseEntity.ok(user);
    }
}

