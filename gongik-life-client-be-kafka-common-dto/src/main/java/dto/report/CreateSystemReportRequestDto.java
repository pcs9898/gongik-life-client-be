package dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSystemReportRequestDto {

  private String userId;

  @Range(min = 1, max = 5)
  private Integer systemCategoryId;

  @NotNull
  @NotBlank
  private String title;

  @NotNull
  @NotBlank
  private String content;

}
