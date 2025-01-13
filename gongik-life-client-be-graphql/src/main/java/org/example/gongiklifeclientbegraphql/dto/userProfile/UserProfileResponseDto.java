package org.example.gongiklifeclientbegraphql.dto.userProfile;

import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponseDto {

  private String id;
  private String email;
  private UserProfileInstitutionDto institution;
  private String name;
  private String bio;
  private String enlistmentDate;
  private String dischargeDate;

  @Data
  @Builder
  private static class UserProfileInstitutionDto {

    private String id;
    private String name;


  }

  public static UserProfileResponseDto fromProto(UserProfileResponse response) {
    return UserProfileResponseDto.builder()
        .id(response.getId())
        .name(response.getName())
        .institution(response.hasInstitution() ? UserProfileInstitutionDto.builder()
            .id(response.getInstitution().getId())
            .name(response.getInstitution().getName())
            .build() : null)
        .bio(response.hasBio() ? response.getBio() : null)
        .enlistmentDate(response.hasEnlistmentDate() ? response.getEnlistmentDate() : null)
        .dischargeDate(response.hasDischargeDate() ? response.getDischargeDate() : null)
        .build();
  }
}
