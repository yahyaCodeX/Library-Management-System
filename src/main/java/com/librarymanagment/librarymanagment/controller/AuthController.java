package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.*;
import com.librarymanagment.librarymanagment.exception.UserException;
import com.librarymanagment.librarymanagment.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid UserDto userDto) throws UserException {
        AuthResponse authResponse=authService.signup(userDto);
        return ResponseEntity.ok(authResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) throws UserException {
        AuthResponse authResponse=authService.login(loginRequestDTO.getUsername(),loginRequestDTO.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) throws UserException {
        authService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Password reset link sent to email if it exists",true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) throws UserException{
        authService.resetPassword(request.getToken(),request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse("Password reset successful",true));
    }
}
