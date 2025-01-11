package org.example.gongiklifeclientbeauthservice.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {

  private String accessToken;
  private String refreshToken;
  private Date accessTokenExpiresAt;
}
