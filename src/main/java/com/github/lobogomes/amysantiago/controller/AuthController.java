package com.github.lobogomes.amysantiago.controller;

import com.github.lobogomes.amysantiago.payload.request.RegisterRequest;
import com.github.lobogomes.amysantiago.payload.response.RegisterResponse;
import com.github.lobogomes.amysantiago.service.AuthenticationService;
import com.github.lobogomes.amysantiago.service.impl.JwtServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authService;
    private final JwtServiceImpl jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){
        log.info("Register request received for email: {}", request.getEmail());
        return authService.registerUser(request);
    }
}

