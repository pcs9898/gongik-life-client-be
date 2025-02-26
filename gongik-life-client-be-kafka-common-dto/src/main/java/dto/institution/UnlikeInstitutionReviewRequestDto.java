package dto.institution;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnlikeInstitutionReviewRequestDto {

  private String institutionReviewId;
  private String userId;
}
