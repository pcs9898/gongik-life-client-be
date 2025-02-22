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
      @Arguments InstitutionRequestDto requestDto
  ) {

    return institutionService.institution(requestDto);
  }

  @MutationMapping
  public CreateInstitutionReviewResponseDto createInstitutionReview(
      @Argument("createInstitutionReviewInput") CreateInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.createInstitutionReview(requestDto);

    } catch (Exception e) {
      log.error("createInstitutionReview error: {}", e);
      throw e;

    }
  }

  @MutationMapping
  public DeleteInstitutionReviewResponseDto deleteInstitutionReview(
      @Arguments DeleteInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      log.info("deleteInstitutionReview requestDto: {}", requestDto);

      return institutionService.deleteInstitutionReview(requestDto);

    } catch (Exception e) {
      log.error("deleteInstitutionReview error: {}", e);
      throw e;

    }
  }

  @MutationMapping
  public LikeInstitutionReviewResponseDto likeInstitutionReview(
      @Argument("likeInstitutionReviewInput") LikeInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.likeInstitutionReview(requestDto);

    } catch (Exception e) {
      log.error("likeInstitutionReview error: {}", e);
      throw e;
    }
  }

  @MutationMapping
  public UnlikeInstitutionReviewResponseDto unlikeInstitutionReview(
      @Argument("unlikeInstitutionReviewInput") UnlikeInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.unlikeInstitutionReview(requestDto);

    } catch (Exception e) {
      log.error("unlikeInstitutionReview error: {}", e);
      throw e;
    }
  }

  @QueryMapping
  public InstitutionReviewResponseDto institutionReview(
      @Argument("institutionReviewInput") InstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.institutionReview(requestDto);

    } catch (Exception e) {
      log.error("institutionReview error: {}", e);
      throw e;
    }
  }

  @QueryMapping
  public InstitutionReviewsResponseDto institutionReviews(
      @Argument("institutionReviewsFilter") InstitutionReviewsRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

      return institutionService.institutionReviews(requestDto);

    } catch (Exception e) {
      log.error("institutionReviews error: {}", e);
      throw e;
    }
  }

  @QueryMapping
  public MyInstitutionReviewsResponseDto myInstitutionReviews(
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      return institutionService.myInstitutionReviews(userId);

    } catch (Exception e) {
      log.error("myInstitutionReview error: {}", e);
      throw e;
    }
  }

  @QueryMapping
  public InstitutionReviewsByInstitutionResponseDto institutionReviewsByInstitution(
      @Argument("institutionReviewsByInstitutionFilter") InstitutionReviewsByInstitutionRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      if (!"-1".equals(userId)) {
        requestDto.setUserId(userId);
      }

      return institutionService.institutionReviewsByInstitution(requestDto);

    } catch (Exception e) {
      log.error("institutionReviews error: {}", e);
      throw e;
    }

  }


}

