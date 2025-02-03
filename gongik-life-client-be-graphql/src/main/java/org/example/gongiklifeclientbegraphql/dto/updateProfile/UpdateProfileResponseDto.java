package org.example.gongiklifeclientbegraphql.dto.updateProfile;

import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileResponseDto {

  private String id;
  private String email;
  private UpdateProfileInstitutionDto institution;
  private String name;
  private String bio;
  private String enlistmentDate;
  private String dischargeDate;

  @Data
  @Builder
  private static class UpdateProfileInstitutionDto {

    private String id;
    private String name;


  }

  public static UpdateProfileResponseDto fromProto(
      UpdateProfileResponse proto) {
    return UpdateProfileResponseDto.builder()
        .id(proto.getId())
        .email(proto.getEmail())
        .name(proto.getName())
        .institution(proto.hasInstitution()
            ? UpdateProfileInstitutionDto.builder()
            .id(proto.getInstitution().getId())
            .name(proto.getInstitution().getName())
            .build() : null)
        .bio(proto.hasBio() ? proto.getBio() : null)
        .enlistmentDate(proto.hasEnlistmentDate() ? proto.getEnlistmentDate() : null)
        .dischargeDate(proto.hasDischargeDate() ? proto.getDischargeDate() : null)
        .build();
  }
}
