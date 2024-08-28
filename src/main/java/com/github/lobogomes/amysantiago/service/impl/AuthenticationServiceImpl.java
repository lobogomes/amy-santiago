package com.github.lobogomes.amysantiago.service.impl;

import com.github.lobogomes.amysantiago.payload.request.RegisterRequest;
import com.github.lobogomes.amysantiago.payload.response.RegisterResponse;
import com.github.lobogomes.amysantiago.service.AuthenticationService;
import org.springframework.http.ResponseEntity;

public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public ResponseEntity<RegisterResponse> registerUser(RegisterRequest request) {
        return null;
    }
}
