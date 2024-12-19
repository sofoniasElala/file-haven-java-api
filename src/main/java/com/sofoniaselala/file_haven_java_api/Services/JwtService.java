package com.sofoniaselala.file_haven_java_api.Services;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sofoniaselala.file_haven_java_api.Exceptions.AccessDeniedException;
import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

/*
 * This manages JSON Web Tokens for user
 */
@Service
public class JwtService {
    private static final String SECRET_KEY = System.getenv("ACCESS_TOKEN_SECRET");
    private static final int DAYS = 14; //Two weeks expiration date


    public String extractUseId(String token) {
        return extractClaim(token, Claims::getSubject); // Claims::getSubject is a method reference that points to the getSubject() method of the Claims class
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extracts a specific claim from a given token 
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); 
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                .parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
          } catch (SignatureException | ExpiredJwtException e) { // Invalid signature or expired token
            throw new AccessDeniedException("Access denied: " + e.getMessage());
          }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userId = extractUseId(token);
        return (Integer.valueOf(userId) == ((AppUserDetails) userDetails).getId() && !isTokenExpired(token));
    }



    public String GenerateToken(int userId){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId);
    }



    private String createToken(Map<String, Object> claims, int userId) {

        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(DAYS, ChronoUnit.DAYS)))
                .signWith(getSignKey()).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}