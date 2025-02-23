package org.example.gongiklifeclientbegraphql.controller;

import dto.institution.LikeInstitutionReviewRequestDto;
import dto.institution.UnlikeInstitutionReviewRequestDto;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.myInstitutionReviews.MyInstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.unlikeInstitutionReview.UnlikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.service.institution.CreateInstitutionReviewService;
import org.example.gongiklifeclientbegraphql.service.institution.DeleteInstitutionReviewService;
import org.example.gongiklifeclientbegraphql.service.institution.GetInstitutionReviewService;
import org.example.gongiklifeclientbegraphql.service.institution.GetInstitutionReviewsService;
import org.example.gongiklifeclientbegraphql.service.institution.GetInstitutionService;
import org.example.gongiklifeclientbegraphql.service.institution.InstitutionReviewsByInstitutionService;
import org.example.gongiklifeclientbegraphql.service.institution.InstitutionService;
import org.example.gongiklifeclientbegraphql.service.institution.LikeInstitutionReviewService;
import org.example.gongiklifeclientbegraphql.service.institution.MyInstitutionReviewsService;
import org.example.gongiklifeclientbegraphql.service.institution.SearchInstitutionsService;
import org.example.gongiklifeclientbegraphql.service.institution.UnlikeInstitutionReviewService;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class InstitutionController {

  private final InstitutionService institutionService;
  private final SearchInstitutionsService searchInstitutionsService;
  private final GetInstitutionService getInstitutionService;
  private final CreateInstitutionReviewService createInstitutionReviewService;
  private final DeleteInstitutionReviewService deleteInstitutionReviewService;
  private final LikeInstitutionReviewService likeInstitutionReviewService;
  private final UnlikeInstitutionReviewService unlikeInstitutionReviewService;
  private final GetInstitutionReviewService getInstitutionReviewService;
  private final GetInstitutionReviewsService getInstitutionReviewsService;
  private final MyInstitutionReviewsService myInstitutionReviewsService;
  private final InstitutionReviewsByInstitutionService institutionReviewsByInstitutionService;

  @QueryMapping
  public SearchInstitutionsResponseDto searchInstitutions(
      @Argument("searchInstitutionsFilter") @Valid SearchInstitutionsRequestDto requestDto
  ) {

    return ControllerExceptionHandlingUtil.handle(() ->
        searchInstitutionsService.searchInstitutions(requestDto)
    );
  }

  @QueryMapping
  public InstitutionResponseDto institution(
      @Argument("institutionInput") @Valid InstitutionRequestDto requestDto
  ) {

    return ControllerExceptionHandlingUtil.handle(() ->
        getInstitutionService.institution(requestDto)
    );
  }

  @MutationMapping
  public CreateInstitutionReviewResponseDto createInstitutionReview(
      @Argument("createInstitutionReviewInput") @Valid CreateInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return createInstitutionReviewService.createInstitutionReview(requestDto);
    });
  }

  @MutationMapping
  public DeleteInstitutionReviewResponseDto deleteInstitutionReview(
      @Argument("deleteInstitutionReviewInput") @Valid DeleteInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return deleteInstitutionReviewService.deleteInstitutionReview(requestDto);
    });
  }

  @MutationMapping
  public LikeInstitutionReviewResponseDto likeInstitutionReview(
      @Argument("likeInstitutionReviewInput") @Valid LikeInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return likeInstitutionReviewService.likeInstitutionReview(requestDto);
    });
  }

  @MutationMapping
  public UnlikeInstitutionReviewResponseDto unlikeInstitutionReview(
      @Argument("unlikeInstitutionReviewInput") @Valid UnlikeInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return unlikeInstitutionReviewService.unlikeInstitutionReview(requestDto);
    });
  }

  @QueryMapping
  public InstitutionReviewResponseDto institutionReview(
      @Argument("institutionReviewInput") @Valid InstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return getInstitutionReviewService.institutionReview(requestDto);
    });
  }

  @QueryMapping
  public InstitutionReviewsResponseDto institutionReviews(
      @Argument("institutionReviewsFilter") @Valid InstitutionReviewsRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return getInstitutionReviewsService.institutionReviews(requestDto);
    });
  }

  @QueryMapping
  public MyInstitutionReviewsResponseDto myInstitutionReviews(
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      return myInstitutionReviewsService.myInstitutionReviews(userId);
    });
  }

  @QueryMapping
  public InstitutionReviewsByInstitutionResponseDto institutionReviewsByInstitution(
      @Argument("institutionReviewsByInstitutionFilter") @Valid InstitutionReviewsByInstitutionRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      if (!"-1".equals(userId)) {
        requestDto.setUserId(userId);
      }

      return institutionReviewsByInstitutionService.institutionReviewsByInstitution(requestDto);
    });
  }
}

