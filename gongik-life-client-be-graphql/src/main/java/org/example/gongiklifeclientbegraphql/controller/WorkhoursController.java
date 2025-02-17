package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.workhours.averageWorkhours.AverageWorkhoursResponseDto;
import org.example.gongiklifeclientbegraphql.service.WorkhoursService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WorkhoursController {

  private final WorkhoursService workhoursService;

  @QueryMapping
  public AverageWorkhoursResponseDto averageWorkhours(
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      return workhoursService.averageWorkhours(userId);

    } catch (Exception e) {
      log.error("Failed to get average workhours", e);
      throw e;
    }
  }
  

}
