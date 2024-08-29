package com.github.lobogomes.amysantiago.service.impl;

import com.github.lobogomes.amysantiago.model.entity.User;
import com.github.lobogomes.amysantiago.payload.request.RegisterRequest;
import com.github.lobogomes.amysantiago.payload.response.RegisterResponse;
import com.github.lobogomes.amysantiago.repository.UserRepository;
import com.github.lobogomes.amysantiago.service.AuthenticationService;
import com.github.lobogomes.amysantiago.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OtpServiceImpl otpService;
  private final EmailService emailService;

  @Override
  public ResponseEntity<RegisterResponse> registerUser(RegisterRequest request) {
    try {
      log.info("Received request to register user with email {}", request.getEmail());
      Optional<User> existingUserOpt =
          userRepository.findByEmail(request.getEmail().trim().toLowerCase());
      if (existingUserOpt.isPresent()) {
        log.info("User already exists with email {}", request.getEmail());
        User existingUser = existingUserOpt.get();
        if (existingUser.getIsVerified()) {
          return ResponseEntity.badRequest()
              .body(RegisterResponse.builder().message("User already exists").build());
        }
        log.info(
            "User already exists but not verified with email {}, so their details will be updated",
            request.getEmail());
        updateUserDetails(existingUser, request);
        String otp = otpService.getOtpForEmail(request.getEmail());
        CompletableFuture<Integer> emailResponse =
            emailService.sendOtpEmail(request.getEmail(), otp);
        if (emailResponse.get() == -1) {
          return ResponseEntity.badRequest()
              .body(
                  RegisterResponse.builder()
                      .message("Failed to send OTP email. Please try again later.")
                      .build());
        }
        userRepository.save(existingUser);
        return ResponseEntity.ok()
            .body(
                RegisterResponse.builder()
                    .message(
                        "An email with OTP has been sent to your email address. Kindly verify.")
                    .build());
      }
      log.info(
          "User does not exist with email {}, so this user will be created", request.getEmail());
      User newUser = createUser(request);
      String otp = otpService.getOtpForEmail(request.getEmail());
      CompletableFuture<Integer> emailResponse = emailService.sendOtpEmail(request.getEmail(), otp);
      if (emailResponse.get() == -1) {
        return ResponseEntity.badRequest()
            .body(
                RegisterResponse.builder()
                    .message("Failed to send OTP email. Please try again later.")
                    .build());
      }
      userRepository.save(newUser);
      log.info("User saved with the email {}", request.getEmail());
      return ResponseEntity.ok()
          .body(
              RegisterResponse.builder()
                  .message("An email with OTP has been sent to your email address. Kindly verify.")
                  .build());

    } catch (MessagingException e) {
      log.error("Failed to send OTP email for user with email {}", request.getEmail(), e);
      return ResponseEntity.internalServerError()
          .body(
              RegisterResponse.builder()
                  .message("Failed to send OTP email. Please try again later.")
                  .build());
    } catch (Exception e) {
      log.error("Failed to register user with email {}", request.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              RegisterResponse.builder()
                  .message("Failed to register user. Please try again later.")
                  .build());
    }
  }

  private User createUser(RegisterRequest request) {
    User user = new User();
    updateUserDetails(user, request);
    return user;
  }

  private void updateUserDetails(User user, RegisterRequest request) {
    user.setFullName(request.getFullName().trim());
    user.setEmail(request.getEmail().trim().toLowerCase());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    user.setUpdatedAt(LocalDate.now());
    user.setIsVerified(false);
  }
}
