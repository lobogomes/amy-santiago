package com.github.lobogomes.amysantiago.payload.response;

import com.github.lobogomes.amysantiago.model.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVerifyResponse {
  private String accessToken;
  private String refreshToken;
  private String fullName;
  private String email;
  private RoleEnum role;
  private boolean isVerified;
}
