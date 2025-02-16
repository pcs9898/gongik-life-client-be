package org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstitutionReviewRequestDto {

  private String institutionId;
  private String userId;
  private int facilityRating;
  private int locationRating;
  private int staffRating;
  private int visitorRating;
  private int vacationFreedomRating;
  private String mainTasks;
  private String prosCons;
  private int averageWorkhours;
  private int workTypeRulesId;
  private int uniformWearingRulesId;
  private int socialServicePeopleCountId;


  public CreateInstitutionReviewRequest toProto() {
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