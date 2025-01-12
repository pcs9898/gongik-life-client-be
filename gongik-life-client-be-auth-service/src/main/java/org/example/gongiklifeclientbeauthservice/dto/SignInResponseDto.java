package org.example.gongiklifeclientbeauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponseDto {

  private SignInUserDto user;
  private String accessToken;
  private String accessTokenExpiresAt;


}