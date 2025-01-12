package org.example.gongiklifeclientbegraphql.dto.signUp;

import com.gongik.userService.domain.service.UserServiceOuterClass;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpUserDto {

  private String id;
  private String email;
  private SignUpInstitutionDto institution;
  private String name;
  private String bio;
  private String enlistmentDate;
  private String dischargeDate;

  public static SignUpUserDto fromProto(UserServiceOuterClass.SignUpUser proto) {
    return SignUpUserDto.builder()
        .id(proto.getId())
        .email(proto.getEmail())
        .institution(
            proto.hasInstitution() ? SignUpInstitutionDto.fromProto(proto.getInstitution()) : null)
        .name(proto.getName())
        .bio(proto.hasBio() ? proto.getBio() : null)
        .enlistmentDate(proto.hasEnlistmentDate() ? proto.getEnlistmentDate() : null)
        .dischargeDate(proto.hasDischargeDate() ? proto.getDischargeDate() : null)
        .build();
  }

  @Data
  @Builder
  private static class SignUpInstitutionDto {

    private String id;
    private String name;

    public static SignUpInstitutionDto fromProto(UserServiceOuterClass.SignUpInstitution proto) {
      return SignUpInstitutionDto.builder()
          .id(proto.getId())
          .name(proto.getName())
          .build();
    }
  }

  // getters and setters
}