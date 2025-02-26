package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionSimpleProjection;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch.InstitutionSearchRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchInstitutionService {

  private final InstitutionSearchRepository institutionSearchRepository;
  private final InstitutionRepository institutionRepository;

  public SearchInstitutionsResponse searchInstitutions(
      SearchInstitutionsRequest request) {

    List<InstitutionSimpleProjection> institutions = institutionRepository.searchInstitutions(
        request.getSearchKeyword(),
        request.getCursor().isEmpty() ? null : UUID.fromString(request.getCursor()),
        request.getPageSize()
    );

    List<SearchInstitution> listSearchInstitution = institutions.stream()
        .map(institution -> {
          return SearchInstitution.newBuilder()
              .setId(institution.getId().toString())
              .setName(institution.getName())
              .setAddress(institution.getAddress())
              .setAverageRating(
                  institution.getAverageRating() != null ? institution.getAverageRating()
                      .floatValue() : 0.0f)
              .build();
        })
        .toList();

    SearchInstitutionsResponse.Builder responseBuilder = SearchInstitutionsResponse.newBuilder()
        .addAllListSearchInstitution(listSearchInstitution);

    String endCursor =
        institutions.isEmpty() ? "" : institutions.get(institutions.size() - 1).getId().toString();
    boolean hasNextPage = institutions.size() == request.getPageSize();

    responseBuilder.setPageInfo(PageInfo.newBuilder()
        .setEndCursor(endCursor)
        .setHasNextPage(hasNextPage)
        .build());

    return responseBuilder.build();
  }
}
