package org.example.gongiklifeclientbegraphql.dto.signUp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {

  private SignUpUserDto user;
  private String accessToken;
  private String accessTokenExpiresAt;

}