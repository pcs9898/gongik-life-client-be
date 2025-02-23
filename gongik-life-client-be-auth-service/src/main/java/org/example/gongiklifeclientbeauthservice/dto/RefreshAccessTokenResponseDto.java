package org.example.gongiklifeclientbeauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshAccessTokenResponseDto {

  @Schema(description = "Access Token", example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIwYzdkNGNmYi01NjNjLTQ3YmMtOTc1ZS04ZmExNTExNmUwZmIiLCJpYXQiOjE3NDAyODE1NDcsImV4cCI6MTc0MDI5OTU0N30.5wlsaKTvl-psQpNEEzKJzQRcJpZwIS8-1X7OcPlnxyC-jdSOQ7GZGbKn4x71mc9L")
  private String accessToken;

  @Schema(description = "Access Token Expire time ", example = "Sun Feb 23 17:32:27 KST 2025", required = true)
  private String accessTokenExpiresAt;


}
