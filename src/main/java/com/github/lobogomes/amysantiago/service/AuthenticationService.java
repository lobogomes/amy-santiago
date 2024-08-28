package com.github.lobogomes.amysantiago.service;

import com.github.lobogomes.amysantiago.payload.request.RegisterRequest;
import com.github.lobogomes.amysantiago.payload.response.RegisterResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<RegisterResponse> registerUser(RegisterRequest registerRequest);
}
