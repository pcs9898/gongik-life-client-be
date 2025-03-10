package org.example.gongiklifeclientbegraphql.dto.user.me;

import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileResponseDto {

  private String id;
  private String email;
  private MyProfileInstitutionDto institution;
  private String name;
  private String bio;
  private String enlistmentDate;
  private String dischargeDate;

  public static MyProfileResponseDto fromMyProfileResponseProto(MyProfileResponse response) {
    return MyProfileResponseDto.builder()
        .id(response.getId())
        .email(response.getEmail())
        .name(response.getName())
        .institution(response.hasInstitution() ? MyProfileInstitutionDto.builder()
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
  private static class MyProfileInstitutionDto {

    private String id;
    private String name;


  }
}
