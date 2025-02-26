package dto.workhours;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageWorkHoursRedisDto implements Serializable {

  private int socialWelfareWorkhours;
  private int publicOrganizationWorkhours;
  private int nationalAgencyWorkhours;
  private int localGovernmentWorkhours;
  private int totalVoteCount;

}
