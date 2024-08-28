package com.github.lobogomes.amysantiago.payload.request;

import com.github.lobogomes.amysantiago.model.enums.RoleEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
  @NotBlank(message = "Name can't be blank")
  private String fullName;

  @NotBlank(message = "Email can't be blank")
  @Email(message = "Invalid email entered")
  private String email;

  @NotBlank(message = "Password can't be blank")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
      message =
          "Password must contain at least 8 characters, one uppercase, one lowercase and one number")
  private String password;

  @NotNull(message = "Please choose a role")
  private RoleEnum role;
}
