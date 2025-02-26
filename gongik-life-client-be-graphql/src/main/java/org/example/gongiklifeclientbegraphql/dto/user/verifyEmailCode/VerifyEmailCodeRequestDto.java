package org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode;

import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailCodeRequestDto {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 6, max = 6)
  private String code;

  public VerifyEmailCodeRequest toVerifyEmailCodeRequestProto() {
    return VerifyEmailCodeRequest.newBuilder()
        .setEmail(email)
        .setCode(code)
        .build();
  }
}
