package com.github.lobogomes.amysantiago.service;

import com.github.lobogomes.amysantiago.model.entity.User;
import com.github.lobogomes.amysantiago.payload.response.RegisterVerifyResponse;
import org.springframework.http.ResponseEntity;

public interface JwtService {
  RegisterVerifyResponse generateJwtToken(User user);

  ResponseEntity<?> generateAccessTokenFromRefreshToken(String refreshToken);
}
