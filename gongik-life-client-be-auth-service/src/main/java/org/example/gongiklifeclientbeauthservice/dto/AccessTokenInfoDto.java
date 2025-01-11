package org.example.gongiklifeclientbeauthservice.dto;

import java.util.Date;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AccessTokenInfoDto {

  private String accessToken;
  private Date accessTokenExpiresAt;

}
