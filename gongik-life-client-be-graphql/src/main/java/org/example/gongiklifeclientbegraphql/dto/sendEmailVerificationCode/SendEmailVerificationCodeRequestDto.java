package org.example.gongiklifeclientbegraphql.dto.sendEmailVerificationCode;


import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import lombok.Data;

@Data
public class SendEmailVerificationCodeRequestDto {

  private String email;

  public SendEmailVerificationCodeRequest toProto() {
    return SendEmailVerificationCodeRequest.newBuilder()
        .setEmail(email)
        .build();
  }

}
