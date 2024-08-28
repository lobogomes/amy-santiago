package com.github.lobogomes.amysantiago.payload.response;

import com.github.lobogomes.amysantiago.model.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponse {
  private String accessToken;
  private String fullName;
  private String email;
  private RoleEnum role;
}
