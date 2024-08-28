package com.github.lobogomes.amysantiago.security;

import com.github.lobogomes.amysantiago.constants.ApplicationConstants;
import com.github.lobogomes.amysantiago.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component

public class JwtHelper {

    public <T> T extractClaims(String jwt , Function<Claims , T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(jwt);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String jwt) {
        return extractClaims(jwt , Claims::getSubject);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String , Object> claims = new HashMap<>();
        claims.put("role" , ((User) userDetails).getRole().name());
        return doGenerateAccessToken(claims, userDetails.getUsername());

    }
    public String generateRefreshToken(UserDetails userDetails) {
        return doGenerateRefreshToken(userDetails.getUsername());
    }

    private String doGenerateRefreshToken(String username) {
        return Jwts.builder()
                .subject("#refresh"+username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ApplicationConstants.REFRESH_TOKEN_VALIDITY_SECONDS * 1000))
                .id(UUID.randomUUID().toString())
                .signWith(getSignInKey())
                .compact();
    }

  public String doGenerateAccessToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(
            new Date(
                System.currentTimeMillis()
                    + ApplicationConstants.REFRESH_TOKEN_VALIDITY_SECONDS * 1000))
        .id(UUID.randomUUID().toString())
        .signWith(getSignInKey())
        .compact();
  }

    public Boolean isTokenValid(String jwt , UserDetails userDetails){
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername())) && isJwtExpired(jwt);
    }

    public Boolean isRefreshTokenValid(String jwt , UserDetails userDetails){
        final String username = extractUsername(jwt).substring(8);
        return (username.equals(userDetails.getUsername())) && isJwtExpired(jwt);
    }

    private boolean isJwtExpired(String jwt) {
        return !extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaims(jwt , Claims::getExpiration);
    }

    private Claims getAllClaimsFromToken(String jwt) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(jwt).getPayload();
    }

    private SecretKey getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(ApplicationConstants.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
