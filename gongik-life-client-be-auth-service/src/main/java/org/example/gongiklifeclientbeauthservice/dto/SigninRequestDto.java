package org.example.gongiklifeclientbeauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SigninRequestDto {

  @Schema(description = "User email", example = "user@example.com", required = true)
  @Email
  private String email;

  @Schema(description = "User password", example = "password123", required = true)
  @NotBlank
  private String password;
}
