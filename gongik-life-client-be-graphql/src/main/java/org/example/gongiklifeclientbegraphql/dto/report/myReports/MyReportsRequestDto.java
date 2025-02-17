package org.example.gongiklifeclientbegraphql.dto.report.myReports;

import com.gongik.reportService.domain.service.ReportServiceOuterClass.MyReportsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReportsRequestDto {

  private String userId;

  private String cursor;

  @Range(min = 1, max = 20)
  private int pageSize;

  public MyReportsRequest toProto() {
    MyReportsRequest.Builder response = MyReportsRequest.newBuilder()
        .setUserId(userId)
        .setPageSize(pageSize);

    if (cursor != null && !cursor.isEmpty()) {
      response.setCursor(cursor);
    }

    return response.build();

  }

}
