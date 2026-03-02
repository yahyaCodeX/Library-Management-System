package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.UserDto;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.mapper.UserMapper;
import com.librarymanagment.librarymanagment.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRespository userRespository;
    private final UserMapper userMapper;


    @Override
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("No authenticated user found in security context");
        }

        String email = authentication.getName();
        User user = userRespository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRespository.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }
}
