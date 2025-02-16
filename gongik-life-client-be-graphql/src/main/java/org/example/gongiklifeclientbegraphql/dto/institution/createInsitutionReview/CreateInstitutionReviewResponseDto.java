package org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.InstitutionShortInfoDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstitutionReviewResponseDto {

  private String id;
  private InstitutionShortInfoDto institution;
  private InstitutionReviewUserDto user;
  private double rating;
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
  private int likeCount;
  private String createdAt;

  public static CreateInstitutionReviewResponseDto fromProto(InstitutionReviewResponse proto) {
    return CreateInstitutionReviewResponseDto.builder()
        .id(proto.getId())
        .institution(InstitutionShortInfoDto.fromProto(proto.getInstitution()))
        .user(InstitutionReviewUserDto.builder()
            .id(proto.getUser().getId())
            .name(proto.getUser().getName())
            .build())
        .rating(proto.getRating())
        .facilityRating(proto.getFacilityRating())
        .locationRating(proto.getLocationRating())
        .staffRating(proto.getStaffRating())
        .visitorRating(proto.getVisitorRating())
        .vacationFreedomRating(proto.getVacationFreedomRating())
        .mainTasks(proto.getMainTasks())
        .prosCons(proto.getProsCons())
        .averageWorkhours(proto.getAverageWorkhours())
        .workTypeRulesId(proto.getWorkTypeRulesId())
        .uniformWearingRulesId(proto.getUniformWearingRulesId())
        .socialServicePeopleCountId(proto.getSocialServicePeopleCountId())
        .likeCount(proto.getLikeCount())
        .createdAt(proto.getCreatedAt())
        .build();
  }
}