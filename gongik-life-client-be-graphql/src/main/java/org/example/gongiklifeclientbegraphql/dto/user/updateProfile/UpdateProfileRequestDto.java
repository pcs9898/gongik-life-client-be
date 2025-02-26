package org.example.gongiklifeclientbegraphql.dto.user.updateProfile;


import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDto {


  private String userId;


  private String name;
  private String bio;
  private String institutionId;
  private String enlistmentDate;
  private String dischargeDate;


  public UpdateProfileRequest toUpdateProfileRequestProto() {
    UpdateProfileRequest.Builder builder = UpdateProfileRequest.newBuilder();

    builder.setUserId(userId);
    if (name != null) {
      builder.setName(name);
    }
    if (bio != null) {
      builder.setBio(bio);
    }
    if (institutionId != null) {
      builder.setInstitutionId(institutionId);
    }
    if (enlistmentDate != null) {
      builder.setEnlistmentDate(enlistmentDate);
    }
    if (dischargeDate != null) {
      builder.setDischargeDate(dischargeDate);
    }

    return builder.build();
  }
}
