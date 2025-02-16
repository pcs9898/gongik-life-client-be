package org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode;

import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import lombok.Data;

@Data
public class VerifyEmailCodeRequestDto {

  private String email;
  private String code;

  public VerifyEmailCodeRequest toProto() {
    return VerifyEmailCodeRequest.newBuilder()
        .setEmail(email)
        .setCode(code)
        .build();
  }
}
