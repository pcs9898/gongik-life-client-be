package org.example.gongiklifeclientbegraphql.dto.verifyEmailCode;

import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import lombok.Data;

@Data
public class verifyEmailCodeRequestDto {

  private String email;
  private String code;

  public VerifyEmailCodeRequest toProto() {
    return VerifyEmailCodeRequest.newBuilder()
        .setEmail(email)
        .setCode(code)
        .build();
  }
}
