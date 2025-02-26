package org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstitutionReviewRequestDto {

  @NotBlank
  private String institutionId;
  private String userId;


  @Range(min = 1, max = 5)
  private int facilityRating;

  @Range(min = 1, max = 5)
  private int locationRating;

  @Range(min = 1, max = 5)
  private int staffRating;

  @Range(min = 1, max = 5)
  private int visitorRating;

  @Range(min = 1, max = 5)
  private int vacationFreedomRating;

  @NotBlank
  private String mainTasks;

  @NotBlank
  private String prosCons;

  @Range(min = 1, max = 480)
  private int averageWorkhours;

  @Range(min = 1, max = 6)
  private int workTypeRulesId;

  @Range(min = 1, max = 3)
  private int uniformWearingRulesId;

  @Range(min = 1, max = 5)
  private int socialServicePeopleCountId;


  public CreateInstitutionReviewRequest toCreateInstitutionReviewRequestProto() {
    return CreateInstitutionReviewRequest.newBuilder()
        .setUserId(this.userId)
        .setInstitutionId(this.institutionId)
        .setFacilityRating(this.facilityRating)
        .setLocationRating(this.locationRating)
        .setStaffRating(this.staffRating)
        .setVisitorRating(this.visitorRating)
        .setVacationFreedomRating(this.vacationFreedomRating)
        .setMainTasks(this.mainTasks)
        .setProsCons(this.prosCons)
        .setAverageWorkhours(this.averageWorkhours)
        .setWorkTypeRulesId(this.workTypeRulesId)
        .setUniformWearingRulesId(this.uniformWearingRulesId)
        .setSocialServicePeopleCountId(this.socialServicePeopleCountId)
        .build();
  }
}