package org.example.gongiklifeclientbeinstitutionservice.dto;

import java.util.UUID;

public interface InstitutionForWorkHoursStatisticsProjection {

  UUID getId();

  Integer getInstitutionCategoryId();

  Integer getAverageWorkhours();

  Integer getReviewCount();
}
