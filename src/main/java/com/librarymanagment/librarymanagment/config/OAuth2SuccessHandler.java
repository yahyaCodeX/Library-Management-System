package com.librarymanagment.librarymanagment.config;

import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.repository.UserRespository;
import com.librarymanagment.librarymanagment.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // Keep this final
    private final UserRespository userRespository;

    // Place @Lazy right here in the constructor argument
    public OAuth2SuccessHandler(@Lazy PasswordEncoder passwordEncoder, UserRespository userRepository,JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRespository = userRepository;
        this.jwtUtil=jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User=(OAuth2User) authentication.getPrincipal();
        String username=oAuth2User.getAttribute("email");
        if(username==null){
            username=oAuth2User.getAttribute("login");
        }
        if(!userRespository.existsByEmail(username)){
            User user=new User();
            user.setEmail(username);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setProvider("GOOGLE");
            user.setRoles(List.of("ROLE_USER"));
            userRespository.save(user);
        }
        String token=jwtUtil.generateToken(username);

        // Redirect to the frontend with the token as a query parameter
        String redirectUrl = "http://localhost:5173/oauth/callback?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

