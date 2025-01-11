package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.document.InstitutionDocument;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch.InstitutionSearchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final InstitutionSearchRepository institutionSearchRepository;
  private final InstitutionRepository institutionRepository;


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

  public GetInstitutionNameResponse getInstitutionName(GetInstitutionNameRequest request) {

    String institutionId = request.getId();
    log.info("getInstitutionName request : {}",
        (institutionId != null && !institutionId.isEmpty()) ? institutionId : "null");
    Institution institution = institutionRepository.findById(UUID.fromString(request.getId()))
        .orElseThrow(() -> new IllegalArgumentException("Institution not found"));

    return GetInstitutionNameResponse.newBuilder()
        .setName(institution.getName())
        .build();
  }
}