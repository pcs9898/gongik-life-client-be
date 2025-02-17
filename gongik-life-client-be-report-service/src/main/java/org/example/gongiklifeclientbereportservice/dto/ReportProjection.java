package org.example.gongiklifeclientbereportservice.dto;

public interface ReportProjection {

  String getId();

  Integer getTypeId();

  Integer getSystemCategoryId(); // null 허용

  String getTargetId(); // null 허용

  Integer getStatusId();

  String getTitle();

  String getCreatedAt(); // 포맷팅된 문자열 (예, "YYYY-MM-DD HH:mm:ss")
}
