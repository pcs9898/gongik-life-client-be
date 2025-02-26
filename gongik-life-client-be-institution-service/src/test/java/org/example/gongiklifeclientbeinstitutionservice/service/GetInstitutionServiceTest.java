package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.DiseaseRestriction;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionCategory;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestriction;
import org.example.gongiklifeclientbeinstitutionservice.entity.RegionalMilitaryOffice;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetInstitutionServiceTest {

  private static final String TEST_INSTITUTION_ID = "123e4567-e89b-12d3-a456-426614174000";

  @Mock
  private InstitutionRepository institutionRepository;

  @Mock
  private InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;

  @InjectMocks
  private GetInstitutionService getInstitutionService;

  @Test
  @DisplayName("기관 정보 조회 성공")
  void institution_Success() {
    // Given
    Institution institution = createTestInstitution();
    List<InstitutionDiseaseRestriction> diseaseRestrictions = createTestDiseaseRestrictions();
    InstitutionRequest request = createTestRequest();

    when(institutionRepository.findById(UUID.fromString(TEST_INSTITUTION_ID)))
        .thenReturn(Optional.of(institution));
    when(institutionDiseaseRestrictionRepository.findByInstitutionId(institution.getId()))
        .thenReturn(diseaseRestrictions);

    // When
    InstitutionResponse response = getInstitutionService.institution(request);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(TEST_INSTITUTION_ID, response.getId()),
        () -> assertEquals("Test Institution", response.getName()),
        () -> assertEquals(2, response.getDiseaseRestrictionsCount()),
        () -> assertTrue(response.getDiseaseRestrictionsList().contains(1)),
        () -> assertTrue(response.getDiseaseRestrictionsList().contains(2))
    );

    verify(institutionRepository).findById(UUID.fromString(TEST_INSTITUTION_ID));
    verify(institutionDiseaseRestrictionRepository).findByInstitutionId(institution.getId());
  }

  @Test
  @DisplayName("존재하지 않는 기관 ID로 조회 시 예외 발생")
  void institution_WhenInstitutionNotFound() {
    // Given
    InstitutionRequest request = createTestRequest();
    when(institutionRepository.findById(any(UUID.class)))
        .thenReturn(Optional.empty());

    // When & Then
    assertThrows(RuntimeException.class,
        () -> getInstitutionService.institution(request));
  }

  @Test
  @DisplayName("질병 제한 정보가 없는 기관 조회")
  void institution_WithNoDiseaseRestrictions() {
    // Given
    Institution institution = createTestInstitution();
    InstitutionRequest request = createTestRequest();

    when(institutionRepository.findById(UUID.fromString(TEST_INSTITUTION_ID)))
        .thenReturn(Optional.of(institution));
    when(institutionDiseaseRestrictionRepository.findByInstitutionId(institution.getId()))
        .thenReturn(Collections.emptyList());

    // When
    InstitutionResponse response = getInstitutionService.institution(request);

    // Then
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(TEST_INSTITUTION_ID, response.getId()),
        () -> assertEquals("Test Institution", response.getName()),
        () -> assertEquals(0, response.getDiseaseRestrictionsCount())
    );
  }

  private Institution createTestInstitution() {
    Institution institution = new Institution();
    institution.setId(UUID.fromString(TEST_INSTITUTION_ID));
    institution.setName("Test Institution");

    // 필요한 다른 필드들 설정
    InstitutionCategory category = new InstitutionCategory();
    category.setId(1L);
    institution.setAddress("충청남도 보령시 중앙로 128");
    institution.setPhoneNumber("041-930-1000");
    institution.setRegionalMilitaryOffice(RegionalMilitaryOffice.builder().id(5L).build());
    institution.setRegion("충청남도");
    institution.setParentInstitution("보령시법원");
    institution.setSexualCriminalRecordRestriction(true);
    institution.setInstitutionCategory(category);
    institution.setReviewCount(0);
    
    return institution;
  }

  private List<InstitutionDiseaseRestriction> createTestDiseaseRestrictions() {
    DiseaseRestriction disease1 = new DiseaseRestriction();
    disease1.setId(1);
    DiseaseRestriction disease2 = new DiseaseRestriction();
    disease2.setId(2);

    InstitutionDiseaseRestriction restriction1 = new InstitutionDiseaseRestriction();
    restriction1.setDiseaseRestriction(disease1);
    InstitutionDiseaseRestriction restriction2 = new InstitutionDiseaseRestriction();
    restriction2.setDiseaseRestriction(disease2);

    return Arrays.asList(restriction1, restriction2);
  }

  private InstitutionRequest createTestRequest() {
    return InstitutionRequest.newBuilder()
        .setInstitutionId(TEST_INSTITUTION_ID)
        .build();
  }
}
