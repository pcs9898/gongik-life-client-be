package org.example.gongiklifeclientbeauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshAccessTokenResponseDto {

  private String accessToken;
  private String accessTokenExpiresAt;


}
