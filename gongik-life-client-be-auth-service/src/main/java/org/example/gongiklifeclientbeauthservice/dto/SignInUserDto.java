package org.example.gongiklifeclientbeauthservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInUserDto {

  private String id;
  private String email;
  private SignInstitutionDto institution;
  private String name;
  private String bio;
  private String enlistmentDate;
  private String dischargeDate;


  @Data
  @Builder
  public static class SignInstitutionDto {

    private String id;
    private String name;

  }

  // getters and setters
}