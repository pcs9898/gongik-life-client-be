package org.example.gongiklifeclientbeinstitutionservice.entity;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewUser;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionShortInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "institution_reviews")
public class InstitutionReview extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "institution_id", nullable = false)
  private Institution institution;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private Double rating;

  @Column(name = "facility_rating", nullable = false)
  private Integer facilityRating;

  @Column(name = "location_rating", nullable = false)
  private Integer locationRating;

  @Column(name = "staff_rating", nullable = false)
  private Integer staffRating;

  @Column(name = "visitor_rating", nullable = false)
  private Integer visitorRating;

  @Column(name = "vacation_freedom_rating", nullable = false)
  private Integer vacationFreedomRating;

  @Column(name = "main_tasks", nullable = false)
  private String mainTasks;

  @Column(name = "pros_cons", nullable = false)
  private String prosCons;

  @Column(name = "average_workhours", nullable = false)
  private Integer averageWorkhours;

  @Column(name = "work_type_rules_id", nullable = false)
  private Integer workTypeRuleId;

  @Column(name = "uniform_wearing_rules_id", nullable = false)
  private Integer uniformWearingRuleId;

  @Column(name = "social_service_people_count_id", nullable = false)
  private Integer socialServicePeopleCountId;

  @Column(name = "like_count", nullable = false)
  private Integer likeCount = 0;

  @Column(name = "deleted_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date deletedAt;


  public static InstitutionReview fromProto(CreateInstitutionReviewRequest request,
      Institution institution, Double rating) {
    return InstitutionReview.builder()
        .userId(UUID.fromString(request.getUserId()))
        .institution(institution)
        .rating(rating)
        .facilityRating(request.getFacilityRating())
        .locationRating(request.getLocationRating())
        .staffRating(request.getStaffRating())
        .visitorRating(request.getVisitorRating())
        .vacationFreedomRating(request.getVacationFreedomRating())
        .mainTasks(request.getMainTasks())
        .prosCons(request.getProsCons())
        .averageWorkhours(request.getAverageWorkhours())
        .workTypeRuleId(request.getWorkTypeRulesId())
        .uniformWearingRuleId(request.getUniformWearingRulesId())
        .socialServicePeopleCountId(request.getSocialServicePeopleCountId())
        .likeCount(0)
        .build();
  }

  public InstitutionReviewResponse toProto(String username) {
    return InstitutionReviewResponse.newBuilder()
        .setId(id.toString())
        .setInstitution(InstitutionShortInfo.newBuilder()
            .setInstitutionId(institution.getId().toString())
            .setInstitutionName(institution.getName())
            .build())
        .setUser(InstitutionReviewUser.newBuilder()
            .setId(userId.toString())
            .setName(username)
            .build())
        .setRating(rating.floatValue())
        .setFacilityRating(facilityRating)
        .setLocationRating(locationRating)
        .setStaffRating(staffRating)
        .setVisitorRating(visitorRating)
        .setVacationFreedomRating(vacationFreedomRating)
        .setMainTasks(mainTasks)
        .setProsCons(prosCons)
        .setAverageWorkhours(averageWorkhours)
        .setWorkTypeRulesId(workTypeRuleId)
        .setUniformWearingRulesId(uniformWearingRuleId)
        .setSocialServicePeopleCountId(socialServicePeopleCountId)
        .setLikeCount(likeCount)
        .setCreatedAt(getCreatedAt().toString())
        .build();
  }
}