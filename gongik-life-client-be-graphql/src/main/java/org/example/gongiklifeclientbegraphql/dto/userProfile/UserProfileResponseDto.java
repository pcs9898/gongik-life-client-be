package org.example.gongiklifeclientbegraphql.dto.userProfile;

import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {

  private String id;
  private String email;
  private UserProfileInstitutionDto institution;
  private String name;
  private String bio;
  private String enlistmentDate;
  private String dischargeDate;

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

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  private static class UserProfileInstitutionDto {

    private String id;
    private String name;


  }
}
