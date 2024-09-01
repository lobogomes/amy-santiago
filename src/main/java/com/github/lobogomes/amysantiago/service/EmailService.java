package com.github.lobogomes.amysantiago.service;

import jakarta.mail.MessagingException;
import java.util.concurrent.CompletableFuture;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
  @Async
  @Retryable(retryFor = MessagingException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
  CompletableFuture<Integer> sendOtpEmail(String to, String otp) throws MessagingException;
}
