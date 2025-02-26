package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.workhours.averageWorkhours.AverageWorkhoursResponseDto;
import org.example.gongiklifeclientbegraphql.service.workhours.WorkhoursService;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
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

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            return workhoursService.averageWorkhours(userId);
        });
    }
}
