package org.example.gongiklifeclientbegraphql.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsResultsDto;
import org.example.gongiklifeclientbegraphql.service.InstitutionService;
import org.springframework.graphql.data.method.annotation.Arguments;
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
}

//@Argument(name = "searchKeyword") String searchKeyword,
//@Argument(name = "cursor") String cursor,
//@Argument(name = "pageSize") int pageSize) {
//
//SearchInstitutionsRequestDto requestDto = new SearchInstitutionsRequestDto();
//    requestDto.setSearchKeyword(searchKeyword);
//    requestDto.setCursor(cursor);
//    requestDto.setPageSize(pageSize);