package org.example.gongiklifeclientbegraphql.controller;

import dto.institution.LikeInstitutionReviewRequestDto;
import dto.institution.UnlikeInstitutionReviewRequestDto;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.createInsitutionReview.CreateInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institutionReview.InstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institutionReviews.InstitutionReviewsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institutionReviews.InstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.myInstitutionReviews.MyInstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsResultsDto;
import org.example.gongiklifeclientbegraphql.dto.unlikeInstitutionReview.UnlikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.service.InstitutionService;
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

  @QueryMapping
  public SearchInstitutionsResultsDto searchInstitutions(
      @Arguments SearchInstitutionsRequestDto requestDto
  ) {

    return institutionService.searchInstitutions(requestDto);
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
      @Argument DeleteInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

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

      log.info("institutionReviews requestDto: {}", requestDto);
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

}

