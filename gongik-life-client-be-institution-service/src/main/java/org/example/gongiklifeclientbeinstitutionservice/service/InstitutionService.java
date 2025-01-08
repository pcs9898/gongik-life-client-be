package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.document.InstitutionDocument;
import org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch.InstitutionSearchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final InstitutionSearchRepository institutionSearchRepository;


  public SearchInstitutionsResponse searchInstitutions(
      SearchInstitutionsRequest request) {
    List<InstitutionDocument> institutions;
    if (request.getCursor().isEmpty()) {
      institutions = institutionSearchRepository.findByNameContainingOrderByIdAsc(
          request.getSearchKeyword(), Pageable.ofSize(request.getPageSize()));
    } else {
      institutions = institutionSearchRepository.findByNameContainingAndIdGreaterThanOrderByIdAsc(
          request.getSearchKeyword(), request.getCursor(),
          Pageable.ofSize(request.getPageSize()));
    }

    SearchInstitutionsResponse.Builder responseBuilder = SearchInstitutionsResponse.newBuilder();
    for (InstitutionDocument institution : institutions) {
      responseBuilder.addListSearchInstitution(institution.toProto());
    }

    String endCursor =
        institutions.isEmpty() ? "" : institutions.get(institutions.size() - 1).getId();
    boolean hasNextPage = institutions.size() == request.getPageSize();

    responseBuilder.setPageInfo(PageInfo.newBuilder()
        .setEndCursor(endCursor)
        .setHasNextPage(hasNextPage)
        .build());

    return responseBuilder.build();
  }
}