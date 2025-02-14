package org.example.gongiklifeclientbeauthservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SigninRequestDto {

  private String email;
  private String password;
}
