package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.MyNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.service.NotificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

  private final NotificationService notificationService;

  @QueryMapping
  public MyNotificationsResponseDto myNotifications(
      @Argument("myNotificationsFilter") @Valid MyNotificationsRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return notificationService.myNotifications(requestDto);

    } catch (Exception e) {
      log.error("Failed to get my notifications", e);
      throw e;
    }
  }

}
