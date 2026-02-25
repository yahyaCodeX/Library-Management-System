package com.librarymanagment.librarymanagment.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secretkey}")
    private String SECRET_KEY;

    public SecretKey getsigninkey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token){
        Claims claims=extractAllClaims(token);
        return claims.getSubject();
    }
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getsigninkey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String generateToken(String username){
        Map<String,Object> claims=new HashMap<>();
        return createToken(claims,username);
    }
    public String createToken(Map<String,Object> claims,String subject){
        return  Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 1000*60*60)) // 1 hour expiration time
                .signWith(getsigninkey())
                .compact();
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String jwt) {
        try {
            extractAllClaims(jwt);  // This validates signature and format
            return !isTokenExpired(jwt);
        } catch (Exception e) {
            return false;
        }
    }


}

