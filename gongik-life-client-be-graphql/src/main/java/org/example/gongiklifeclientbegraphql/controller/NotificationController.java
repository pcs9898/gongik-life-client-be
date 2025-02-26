package org.example.gongiklifeclientbegraphql.controller;

import dto.notification.DeleteAllNotificationsRequestDto;
import dto.notification.DeleteNotificationRequestDto;
import dto.notification.MarkAllNotificationsAsReadRequestDto;
import dto.notification.MarkNotificationAsReadRequestDto;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteAllNotifications.DeleteAllNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.deleteNotification.DeleteNotificationResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markAllNotificationsAsRead.MarkAllNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.markNotificationAsRead.MarkNotificationAsReadResponseDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsResponseDto;
import org.example.gongiklifeclientbegraphql.service.notification.*;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final MyNotificationsService myNotificationsService;
    private final MarkNotificationAsReadService markNotificationAsReadService;
    private final MarkAllNotificationsAsReadService markAllNotificationsAsReadService;
    private final DeleteNotificationService deleteNotificationService;
    private final DeleteAllNotificationsService deleteAllNotificationsService;

    @QueryMapping
    public MyNotificationsResponseDto myNotifications(
            @Argument("myNotificationsFilter") @Valid MyNotificationsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return myNotificationsService.myNotifications(requestDto);
        });
    }

    @MutationMapping
    public MarkNotificationAsReadResponseDto markNotificationAsRead(
            @Argument("markNotificationAsReadInput") @Valid MarkNotificationAsReadRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return markNotificationAsReadService.markNotificationAsRead(requestDto);
        });
    }

    @MutationMapping
    public MarkAllNotificationAsReadResponseDto markAllNotificationsAsRead(
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {

            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            return markAllNotificationsAsReadService.markAllNotificationsAsRead(
                    MarkAllNotificationsAsReadRequestDto.builder().userId(userId).build());
        });
    }

    @MutationMapping
    public DeleteNotificationResponseDto deleteNotification(
            @Argument("deleteNotificationInput") DeleteNotificationRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return deleteNotificationService.deleteNotification(requestDto);
        });
    }


    @MutationMapping
    public DeleteAllNotificationsResponseDto deleteAllNotifications(
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {

            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            return deleteAllNotificationsService.deleteAllNotifications(
                    DeleteAllNotificationsRequestDto.builder().userId(userId).build());
        });
    }

}
