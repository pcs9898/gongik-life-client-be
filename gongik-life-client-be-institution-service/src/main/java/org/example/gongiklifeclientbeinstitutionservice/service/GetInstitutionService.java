package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInstitutionService {

  private final InstitutionRepository institutionRepository;
  private final InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;

  @Transactional(readOnly = true)
  public InstitutionResponse institution(InstitutionRequest request) {
    Institution institution = findInstitutionById(request.getInstitutionId());
    List<Integer> diseaseRestrictionIds = getDiseaseRestrictionIds(institution);

    return buildInstitutionResponse(institution, diseaseRestrictionIds);
  }

  private Institution findInstitutionById(String institutionId) {
    return institutionRepository.findById(UUID.fromString(institutionId))
        .orElseThrow(() -> new NotFoundException("Institution not found, wrong institution id"));
  }

  private List<Integer> getDiseaseRestrictionIds(Institution institution) {
    return institutionDiseaseRestrictionRepository
        .findByInstitutionId(institution.getId())
        .stream()
        .map(restriction -> restriction.getDiseaseRestriction().getId())
        .collect(Collectors.toList());
  }

  private InstitutionResponse buildInstitutionResponse(Institution institution,
      List<Integer> diseaseRestrictionIds) {
    InstitutionResponse.Builder responseBuilder = institution.toInstitutionResponseProto();
    responseBuilder.addAllDiseaseRestrictions(diseaseRestrictionIds);

    return responseBuilder.build();
  }
}
