package org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode;


import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmailVerificationCodeRequestDto {

  @NotBlank
  @Email
  private String email;

  public SendEmailVerificationCodeRequest toSendEmailVerificationCodeRequestProto() {
    return SendEmailVerificationCodeRequest.newBuilder()
        .setEmail(email)
        .build();
  }

}
