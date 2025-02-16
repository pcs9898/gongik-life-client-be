package org.example.gongiklifeclientbeinstitutionservice.dto;

import java.util.UUID;

public interface InstitutionSimpleProjection {

  UUID getId();

  String getName();

  String getAddress();

  Double getAverageRating();


}