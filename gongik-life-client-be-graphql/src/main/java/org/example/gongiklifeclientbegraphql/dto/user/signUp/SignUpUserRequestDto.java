package org.example.gongiklifeclientbegraphql.dto.user.signUp;

import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserRequestDto {

  @NotBlank
  private String name;

  @Email
  private String email;

  @NotBlank
  private String password;

  @NotBlank
  private String confirmPassword;


  private String bio;

  private String institutionId;

  private String enlistmentDate;

  private String dischargeDate;

  public SignUpRequest toSignUpRequestProto() {
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