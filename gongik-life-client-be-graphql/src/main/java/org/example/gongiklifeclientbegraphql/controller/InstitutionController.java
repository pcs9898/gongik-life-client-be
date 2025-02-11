package org.example.gongiklifeclientbegraphql.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.createInsitutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsResultsDto;
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
  public InstitutionReviewResponseDto createInstitutionReview(
      @Argument("createInstitutionReviewInput") CreateInstitutionReviewRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    try {
      String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

      requestDto.setUserId(userId);

//      log.info("createInstitutionReview requestDto: {}", requestDto);
//      InstitutionReviewResponse response = InstitutionReviewResponse.newBuilder()
//          .setId("tempId")
//          .setInstitutionId(requestDto.getInstitutionId())
//          .setUser(
//              InstitutionReviewUser.newBuilder().setId("tempUserId").setName("tempUserName")
//                  .build())
//          .setRating(5.0f)
//          .setFacilityRating(requestDto.getFacilityRating())
//          .setLocationRating(requestDto.getLocationRating())
//          .setStaffRating(requestDto.getStaffRating())
//          .setVisitorRating(requestDto.getVisitorRating())
//          .setVacationFreedomRating(requestDto.getVacationFreedomRating())
//          .setMainTasks(requestDto.getMainTasks())
//          .setProsCons(requestDto.getProsCons())
//          .setAverageWorkhours(requestDto.getAverageWorkhours())
//          .setWorkTypeRulesId(requestDto.getWorkTypeRulesId())
//          .setUniformWearingRulesId(requestDto.getUniformWearingRulesId())
//          .setSocialServicePeopleCountId(requestDto.getSocialServicePeopleCountId())
//          .setLikeCount(0)
//          .setCreatedAt("2025-02-10T14:14:26.682+09:00")
//          .build();
//
//      institutionService.createInstitutionReview(requestDto);
      return institutionService.createInstitutionReview(requestDto);


    } catch (Exception e) {
      log.error("createInstitutionReview error: {}", e);
      throw e;

    }

  }

}

