package com.github.lobogomes.amysantiago.service;


public interface OtpService {
  String generateOtp();

  String getOtpForEmail(String email);
}
