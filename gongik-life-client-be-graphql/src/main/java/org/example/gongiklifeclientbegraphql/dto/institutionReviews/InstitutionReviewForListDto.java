package org.example.gongiklifeclientbegraphql.dto.institutionReviews;


import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewForList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.InstitutionReviewInstitutionDto;
import org.example.gongiklifeclientbegraphql.dto.institutionReview.InstitutionReviewUserDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class InstitutionReviewForListDto {

  private String id;
  private InstitutionReviewInstitutionDto institution;
  private InstitutionReviewUserDto user;
  private double rating;
  private String mainTasks;
  private String prosCons;
  private int averageWorkhours;
  private int likeCount;
  private String createdAt;
  private Boolean isLiked;


  public static InstitutionReviewForListDto fromProto(
      InstitutionReviewForList institutionReviewForList) {

    return InstitutionReviewForListDto.builder()
        .id(institutionReviewForList.getId())
        .institution(
            InstitutionReviewInstitutionDto.fromProto(institutionReviewForList.getInstitution()))
        .user(InstitutionReviewUserDto.fromProto(institutionReviewForList.getUser()))
        .rating(institutionReviewForList.getRating())
        .mainTasks(institutionReviewForList.getMainTasks())
        .prosCons(institutionReviewForList.getProsCons())
        .averageWorkhours(institutionReviewForList.getAverageWorkhours())
        .likeCount(institutionReviewForList.getLikeCount())
        .createdAt(institutionReviewForList.getCreatedAt())
        .isLiked(institutionReviewForList.getIsLiked())
        .build();
  }
}
