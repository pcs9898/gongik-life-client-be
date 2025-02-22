package org.example.gongiklifeclientbeinstitutionservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionSimpleProjection;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

  @Mock
  private InstitutionRepository institutionRepository;

  @InjectMocks
  private InstitutionService institutionService;

  @Nested
  @DisplayName("searchInstitutions 메서드는")
  class SearchInstitutionsMethod {

    private SearchInstitutionsRequest request;

    @BeforeEach
    void setUp() {
      request = SearchInstitutionsRequest.newBuilder()
          .setSearchKeyword("테스트")
          .setPageSize(10)
          .setCursor("")
          .build();
    }

    @Test
    @DisplayName("성공: 검색 결과가 있을 경우, 기관 목록과 페이지 정보를 반환한다")
    void success_returnsInstitutionsAndPageInfo() {
      // given
      UUID institutionId = UUID.randomUUID();
      List<InstitutionSimpleProjection> mockInstitutions = List.of(
          new InstitutionSimpleProjection() {
            @Override
            public UUID getId() {
              return institutionId;
            }

            @Override
            public String getName() {
              return "테스트 병원";
            }

            @Override
            public String getAddress() {
              return "서울시 강남구";
            }

            @Override
            public Double getAverageRating() {
              return 4.5;
            }
          }
      );

      when(institutionRepository.searchInstitutions(
          eq("테스트"),
          isNull(),
          eq(10)
      )).thenReturn(mockInstitutions);

      // when
      SearchInstitutionsResponse response = institutionService.searchInstitutions(request);

      // then
      assertAll(
          () -> assertEquals(1, response.getListSearchInstitutionCount()),
          () -> {
            SearchInstitution institution = response.getListSearchInstitution(0);
            assertEquals(institutionId.toString(), institution.getId());
            assertEquals("테스트 병원", institution.getName());
            assertEquals("서울시 강남구", institution.getAddress());
            assertEquals(4.5f, institution.getAverageRating());
          },
          () -> {
            PageInfo pageInfo = response.getPageInfo();
            assertEquals(institutionId.toString(), pageInfo.getEndCursor());
            assertFalse(pageInfo.getHasNextPage());
          }
      );
    }

    @Test
    @DisplayName("성공: 검색 결과가 없을 경우, 빈 목록과 페이지 정보를 반환한다")
    void success_whenNoResults_returnsEmptyResponse() {
      // given
      when(institutionRepository.searchInstitutions(
          anyString(),
          any(),
          anyInt()
      )).thenReturn(Collections.emptyList());

      // when
      SearchInstitutionsResponse response = institutionService.searchInstitutions(request);

      // then
      assertAll(
          () -> assertEquals(0, response.getListSearchInstitutionCount()),
          () -> assertEquals("", response.getPageInfo().getEndCursor()),
          () -> assertFalse(response.getPageInfo().getHasNextPage())
      );
    }

    @Test
    @DisplayName("성공: pageSize만큼 결과가 있을 경우, hasNextPage가 true이다")
    void success_whenResultsSizeEqualsPageSize_hasNextPageIsTrue() {
      // given
      List<InstitutionSimpleProjection> mockInstitutions = Arrays.asList(
          createMockProjection(UUID.randomUUID()),
          createMockProjection(UUID.randomUUID())
      );

      request = SearchInstitutionsRequest.newBuilder()
          .setSearchKeyword("테스트")
          .setPageSize(2)
          .setCursor("")
          .build();

      when(institutionRepository.searchInstitutions(
          anyString(),
          any(),
          eq(2)
      )).thenReturn(mockInstitutions);

      // when
      SearchInstitutionsResponse response = institutionService.searchInstitutions(request);

      // then
      assertTrue(response.getPageInfo().getHasNextPage());
    }

    @Test
    @DisplayName("실패: 잘못된 커서 UUID가 주어진 경우 예외가 발생한다")
    void fail_whenInvalidCursor_throwsException() {
      // given
      SearchInstitutionsRequest invalidRequest = SearchInstitutionsRequest.newBuilder()
          .setSearchKeyword("테스트")
          .setPageSize(10)
          .setCursor("invalid-uuid")
          .build();

      // when & then
      assertThrows(IllegalArgumentException.class, () ->
          institutionService.searchInstitutions(invalidRequest)
      );
    }

    private InstitutionSimpleProjection createMockProjection(UUID id) {
      return new InstitutionSimpleProjection() {
        @Override
        public UUID getId() {
          return id;
        }

        @Override
        public String getName() {
          return "테스트 기관";
        }

        @Override
        public String getAddress() {
          return "테스트 주소";
        }

        @Override
        public Double getAverageRating() {
          return 4.0;
        }
      };
    }
  }
}
