package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass;
import com.gongik.reportService.domain.service.ReportServiceOuterClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PageInfoDto {

  private String endCursor;
  private boolean hasNextPage;

  public static PageInfoDto fromProto(PageInfo proto) {
    PageInfoDto dto = new PageInfoDto();
    dto.setEndCursor(proto.getEndCursor());
    dto.setHasNextPage(proto.getHasNextPage());
    return dto;
  }

  public static PageInfoDto fromCommunityServiceProto(CommunityServiceOuterClass.PageInfo proto) {
    PageInfoDto dto = new PageInfoDto();
    dto.setEndCursor(proto.getEndCursor());
    dto.setHasNextPage(proto.getHasNextPage());
    return dto;
  }

  public static PageInfoDto fromReportServiceProto(ReportServiceOuterClass.PageInfo pageInfo) {
    PageInfoDto dto = new PageInfoDto();

    dto.setEndCursor(pageInfo.getEndCursor());

    dto.setHasNextPage(pageInfo.getHasNextPage());
    return dto;
  }

  public static PageInfoDto fromMyNotificationResponseProto(
      NotificationServiceOuterClass.PageInfo pageInfo) {
    PageInfoDto dto = new PageInfoDto();

    dto.setEndCursor(pageInfo.getEndCursor());

    dto.setHasNextPage(pageInfo.getHasNextPage());
    return dto;
  }
}