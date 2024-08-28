package com.github.lobogomes.amysantiago.service;

import com.github.lobogomes.amysantiago.exception.ResourceNotFoundException;
import com.github.lobogomes.amysantiago.model.entity.User;
import com.github.lobogomes.amysantiago.payload.response.GeneralAPIResponse;
import com.github.lobogomes.amysantiago.payload.response.RefreshTokenResponse;
import com.github.lobogomes.amysantiago.payload.response.RegisterVerifyResponse;
import com.github.lobogomes.amysantiago.repository.UserRepository;
import com.github.lobogomes.amysantiago.security.JwtHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
  private final JwtHelper jwtHelper;
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;

  public RegisterVerifyResponse generateJwtToken(User user) {
    String myAccessToken = jwtHelper.generateAccessToken(user);
    String myRefreshToken = jwtHelper.generateRefreshToken(user);
    return RegisterVerifyResponse.builder()
        .accessToken(myAccessToken)
        .refreshToken(myRefreshToken)
        .fullName(user.getFullName())
        .email(user.getEmail())
        .role(user.getRole())
        .isVerified(user.isAccountNonExpired())
        .build();
  }

  public ResponseEntity<?> generateAccessTokenFromRefreshToken(String refreshToken) {
    if (Objects.nonNull(refreshToken) && !refreshToken.isEmpty()) {
      try {
        String username = jwtHelper.extractUsername(refreshToken);
        if (username.startsWith("#refresh")) {
          String finalUsername = username.substring(8);
          UserDetails userDetails = userDetailsService.loadUserByUsername(finalUsername);
          User user =
              userRepository
                  .findByEmail(finalUsername)
                  .orElseThrow(
                      () ->
                          new ResourceNotFoundException(
                              "User not found with email " + finalUsername));
          if (jwtHelper.isRefreshTokenValid(refreshToken, userDetails)) {
            String accessToken = jwtHelper.generateAccessToken(userDetails);
            return ResponseEntity.ok()
                .body(
                    RefreshTokenResponse.builder()
                        .accessToken(accessToken)
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build());
          }
          return ResponseEntity.badRequest()
              .body(GeneralAPIResponse.builder().message("Expired refresh token").build());
        }
        return ResponseEntity.badRequest()
            .body(GeneralAPIResponse.builder().message("Invalid refresh token").build());
      } catch (MalformedJwtException ex) {
        return ResponseEntity.badRequest()
            .body(GeneralAPIResponse.builder().message("Invalid refresh token").build());
      } catch (ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(GeneralAPIResponse.builder().message("Invalid refresh token").build());
      } catch (ExpiredJwtException ex) {
        return ResponseEntity.badRequest()
            .body(GeneralAPIResponse.builder().message("Expired refresh token").build());
      }
    }
    return ResponseEntity.badRequest()
        .body(GeneralAPIResponse.builder().message("Refresh token is null").build());
  }
}
