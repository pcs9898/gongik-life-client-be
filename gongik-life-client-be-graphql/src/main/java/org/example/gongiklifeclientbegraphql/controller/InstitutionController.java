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
import org.example.gongiklifeclientbegraphql.service.InstitutionService;
import org.example.gongiklifeclientbegraphql.service.institution.GetInstitutionService;
import org.example.gongiklifeclientbegraphql.service.institution.SearchInstitutionsService;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.Arguments;
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
      @Argument("createInstitutionReviewInput") CreateInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.createInstitutionReview(requestDto);
    });
  }

  @MutationMapping
  public DeleteInstitutionReviewResponseDto deleteInstitutionReview(
      @Arguments DeleteInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.deleteInstitutionReview(requestDto);
    });
  }

  @MutationMapping
  public LikeInstitutionReviewResponseDto likeInstitutionReview(
      @Argument("likeInstitutionReviewInput") LikeInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.likeInstitutionReview(requestDto);
    });
  }

  @MutationMapping
  public UnlikeInstitutionReviewResponseDto unlikeInstitutionReview(
      @Argument("unlikeInstitutionReviewInput") UnlikeInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.unlikeInstitutionReview(requestDto);
    });
  }

  @QueryMapping
  public InstitutionReviewResponseDto institutionReview(
      @Argument("institutionReviewInput") InstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {

    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.institutionReview(requestDto);
    });
  }

  @QueryMapping
  public InstitutionReviewsResponseDto institutionReviews(
      @Argument("institutionReviewsFilter") InstitutionReviewsRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.institutionReviews(requestDto);
    });
  }

  @QueryMapping
  public MyInstitutionReviewsResponseDto myInstitutionReviews(
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      return institutionService.myInstitutionReviews(userId);
    });
  }

  @QueryMapping
  public InstitutionReviewsByInstitutionResponseDto institutionReviewsByInstitution(
      @Argument("institutionReviewsByInstitutionFilter") InstitutionReviewsByInstitutionRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(() -> {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      if (!"-1".equals(userId)) {
        requestDto.setUserId(userId);
      }

      return institutionService.institutionReviewsByInstitution(requestDto);
    });
  }
}

