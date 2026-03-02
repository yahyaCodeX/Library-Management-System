package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.UserDto;
import com.librarymanagment.librarymanagment.entity.User;

import java.util.List;

public interface UserService {
    public User getCurrentUser();
    public List<UserDto> getAllUsers();
}
