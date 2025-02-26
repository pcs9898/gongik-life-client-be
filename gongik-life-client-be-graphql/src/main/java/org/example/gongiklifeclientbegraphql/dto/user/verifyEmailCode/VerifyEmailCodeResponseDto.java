package org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode;

import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailCodeResponseDto {

  private boolean success;

  public static VerifyEmailCodeResponseDto fromVerifyEmailCodeResponseProto(
      VerifyEmailCodeResponse proto) {
    return VerifyEmailCodeResponseDto.builder()
        .success(proto.getSuccess())
        .build();
  }

}
