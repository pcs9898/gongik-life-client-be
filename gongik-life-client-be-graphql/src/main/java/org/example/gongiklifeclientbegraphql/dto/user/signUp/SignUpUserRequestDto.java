package org.example.gongiklifeclientbegraphql.dto.user.signUp;

import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserRequestDto {

  private String name;
  private String email;
  private String password;
  private String confirmPassword;
  private String bio;
  private String institutionId;
  private String enlistmentDate;
  private String dischargeDate;

  public SignUpRequest toProto() {
    return SignUpRequest.newBuilder()
        .setName(name)
        .setEmail(email)
        .setPassword(password)
        .setConfirmPassword(confirmPassword)
        .setBio(bio != null ? bio : "")
        .setInstitutionId(institutionId != null ? institutionId : "")
        .setEnlistmentDate(enlistmentDate != null ? enlistmentDate : "")
        .setDischargeDate(dischargeDate != null ? dischargeDate : "")
        .build();
  }
}