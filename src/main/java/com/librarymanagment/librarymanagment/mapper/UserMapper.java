package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.UserDto;
import com.librarymanagment.librarymanagment.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Converts User entity to UserDto (without password for security)
     * @param user User entity
     * @return UserDto with user details (password excluded)
     */
    public  UserDto toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setPhone(user.getPhone());
        userDto.setRoles(user.getRoles());
        userDto.setLastLogin(user.getLastLogin());
        // Password is intentionally excluded for security

        return userDto;
    }
    public List<UserDto> toDTOList(List<User> users){
        return  users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public Set<UserDto> toDTOSet(Set<User> users){
        return  users.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Converts UserDto to User entity (for creating new users)
     * Note: Password should be encoded before calling this method
     * @param userDto UserDto
     * @return User entity
     */
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setPhone(userDto.getPhone());
        user.setPassword(userDto.getPassword()); // Note: Should be encoded before setting
        user.setRoles(userDto.getRoles());
        user.setLastLogin(userDto.getLastLogin());

        return user;
    }

    /**
     * Updates existing User entity from UserDto (for update operations)
     * @param userDto UserDto with updated values
     * @param user Existing User entity to update
     */
    public void updateEntityFromDto(UserDto userDto, User user) {
        if (userDto == null || user == null) {
            return;
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        if (userDto.getRoles() != null) {
            user.setRoles(userDto.getRoles());
        }
        // Password should be updated separately with proper encoding
    }
}

