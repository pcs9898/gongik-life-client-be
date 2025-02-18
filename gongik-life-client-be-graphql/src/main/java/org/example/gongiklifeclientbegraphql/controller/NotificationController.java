package org.example.gongiklifeclientbegraphql.controller;

import dto.notification.MarkAllNotificationsAsReadRequestDto;
import dto.notification.MarkNotificationAsReadRequestDto;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.markAllNotificationsAsRead.MarkAllNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead.MarkNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.service.NotificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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

  @MutationMapping
  public MarkNotificationAsReadResponseDto markNotificationAsRead(
      @Argument("markNotificationAsReadInput") @Valid MarkNotificationAsReadRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return notificationService.markNotificationAsRead(requestDto);

    } catch (Exception e) {
      log.error("Failed to mark notification as read", e);
      throw e;
    }
  }

  @MutationMapping
  public MarkAllNotificationAsReadResponseDto markAllNotificationsAsRead(
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {

      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      return notificationService.markAllNotificationsAsRead(MarkAllNotificationsAsReadRequestDto
          .builder().userId(userId).build());

    } catch (Exception e) {
      log.error("Failed to mark all notifications as read", e);
      throw e;
    }
  }

}
