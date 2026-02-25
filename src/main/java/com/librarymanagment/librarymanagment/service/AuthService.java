package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.AuthResponse;
import com.librarymanagment.librarymanagment.dto.UserDto;
import com.librarymanagment.librarymanagment.exception.UserException;

public interface AuthService {
    AuthResponse login(String username, String password) throws UserException;
    AuthResponse signup(UserDto req) throws UserException;
    void createPasswordResetToken(String email) throws UserException;
    void resetPassword(String token, String newPassword) throws UserException;
}
