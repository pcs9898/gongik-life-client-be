package org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailVerificationCodeResponseDto {

  private boolean success;

  public static SendEmailVerificationCodeResponseDto fromSendEmailVerificationCodeResponseProto(
      com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse proto) {
    return SendEmailVerificationCodeResponseDto.builder()
        .success(proto.getSuccess())
        .build();
  }
}
