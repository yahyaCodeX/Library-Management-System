package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.AuthResponse;
import com.librarymanagment.librarymanagment.dto.UserDto;
import com.librarymanagment.librarymanagment.entity.PasswordResetToken;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.UserException;
import com.librarymanagment.librarymanagment.mapper.UserMapper;
import com.librarymanagment.librarymanagment.repository.PasswordResetTokenRepository;
import com.librarymanagment.librarymanagment.repository.UserRespository;
import com.librarymanagment.librarymanagment.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRespository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Transactional
    @Override
    public AuthResponse login(String username, String password) throws UserException {
        try {
            // ✅ LOGIN REQUIRES AUTHENTICATION - Verify credentials against database
             authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // If authentication successful, load user and generate token
            User user = userRespository.findByEmail(username);
            if (user == null) {
                throw new UserException("Invalid email or password");
            }

            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRespository.save(user);

            // Generate JWT token
            String jwt = jwtUtil.generateToken(user.getEmail());

            // Use UserMapper to convert User to UserDto (password excluded)
            UserDto userDto = userMapper.toDTO(user);

            // Create and return AuthResponse with JWT token
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setMessage("Login successful");
            authResponse.setTitle("Success");
            authResponse.setUser(userDto);

            return authResponse;

        } catch (BadCredentialsException e) {
            throw new UserException("Invalid email or password");
        }
    }

    @Override
    public AuthResponse signup(UserDto req) throws UserException {
        User user=userRespository.findByEmail(req.getEmail());
        if(user!=null){
            throw new UserException("User Already Exists");
        }
        User createduser =new User();
        createduser.setEmail(req.getEmail());
        createduser.setPassword(passwordEncoder.encode(req.getPassword()));
        createduser.setPhone(req.getPhone());
        createduser.setFullName(req.getFullName());
        createduser.setLastLogin(LocalDateTime.now());
        createduser.setProvider("LOCAL");
        createduser.setRoles(List.of("ROLE_USER"));

        User savedUser = userRespository.save(createduser);

        // Generate JWT token using the user's email
        String jwt = jwtUtil.generateToken(savedUser.getEmail());

        // Create and return AuthResponse with JWT token
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Signup successful");
        authResponse.setTitle("Success");
        // Use UserMapper to convert User to UserDto (password excluded)
        authResponse.setUser(userMapper.toDTO(savedUser));
        return authResponse;
    }

    @Transactional
    @Override
    public void createPasswordResetToken(String email) throws UserException {
        // 1. Find the user by email
        User user = userRespository.findByEmail(email);
        if (user == null) {
            throw new UserException("No account found with email: " + email);
        }

        // 2. Delete any existing reset tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // 3. Generate a unique token (UUID)
        String token = UUID.randomUUID().toString();

        // 4. Create a PasswordResetToken entity with 15-minute expiry
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(15)
        );
        passwordResetTokenRepository.save(resetToken);

        // 5. Send the reset link via email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    @Override
    public void resetPassword(String token, String newPassword) throws UserException {
        // 1. Find the token in the database
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException("Invalid password reset token"));

        // 2. Check if already used
        if (resetToken.isUsed()) {
            throw new UserException("This password reset token has already been used");
        }

        // 3. Check if expired
        if (resetToken.isExpired()) {
            throw new UserException("Password reset token has expired. Please request a new one");
        }

        // 4. Update the user's password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRespository.save(user);

        // 5. Mark the token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
