package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.UserDto;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.mapper.UserMapper;
import com.librarymanagment.librarymanagment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
