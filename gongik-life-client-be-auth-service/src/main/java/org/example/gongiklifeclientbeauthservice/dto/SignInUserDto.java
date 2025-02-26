package org.example.gongiklifeclientbeauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInUserDto {

  @Schema(description = "User ID", example = "0c7d4cfb-563c-47bc-975e-8fa15116e0fb", required = true)
  private String id;

  @Schema(description = "User email", example = "user@user.com", required = true)
  private String email;

  @Schema(description = "User Institution", example = "User Institution info", required = false)
  private SignInstitutionDto institution;

  @Schema(description = "User name", example = "User Name", required = true)
  private String name;

  @Schema(description = "User bio", example = "Hi~ i'm user!", required = false)
  private String bio;

  @Schema(description = "Social service enlistment date", example = "2020-01-01", required = false)
  private String enlistmentDate;

  @Schema(description = "Social service discharge date", example = "2021-01-01", required = false)
  private String dischargeDate;


  @Data
  @Builder
  public static class SignInstitutionDto {

    @Schema(description = "Institution ID", example = "0c7d4cfb-563c-47bc-975e-8fa15116e0fb", required = true)
    private String id;

    @Schema(description = "Institution name", example = "대전지방법원홍성지원보령시법원", required = true)
    private String name;

  }

  // getters and setters
}