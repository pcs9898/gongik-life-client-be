package org.example.gongiklifeclientbegraphql.service.institution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.List;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SearchInstitutionsServiceTest {

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  // SearchInstitutionsService 내부에서는 @GrpcClient로 주입했던 blocking stub을 생성자나 필드 주입으로 대체하여 테스트 시 @InjectMocks를 사용할 수 있도록 합니다.
  @InjectMocks
  private SearchInstitutionsService searchInstitutionsService;


  private SearchInstitutionsRequestDto requestDto;
  private SearchInstitutionsResponse dummyResponse;


  @BeforeEach
  void setUp() {

    requestDto = SearchInstitutionsRequestDto.builder()
        .searchKeyword("dummy").pageSize(5).build();

    dummyResponse = SearchInstitutionsResponse.newBuilder()
        .addAllListSearchInstitution(List.of(
            SearchInstitution.newBuilder().setId("1234").setName("dummy").build()
        ))
        .setPageInfo(PageInfo.newBuilder().setHasNextPage(false).setEndCursor("1234").build())
        .build();
  }

  @DisplayName("Test SearchInstitutions Success")
  @Test
  void givenRequestDtoNResponseDto_whenSearchInstitution_thenSuccess() throws Exception {
    // Given

    // When
    when(institutionBlockingStub.searchInstitutions(any(SearchInstitutionsRequest.class)))
        .thenReturn(dummyResponse);

    SearchInstitutionsResponseDto result = searchInstitutionsService.searchInstitutions(requestDto);

    // Then
    assertEquals(1, result.getListSearchInstitution().size());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertEquals("1234", result.getPageInfo().getEndCursor());

  }

  @DisplayName("Test SearchInstitutions Failure")
  @Test
  void givenRequestDtoNResponseDto_whenSearchInstitution_thenFailure() throws Exception {
    // Given

    // When
    when(institutionBlockingStub.searchInstitutions(any(SearchInstitutionsRequest.class)))
        .thenThrow(new RuntimeException("gRPC error"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      searchInstitutionsService.searchInstitutions(requestDto);
    });

    // Then
    String expectedMessage = "Error occurred in SearchInstitutionsService";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.startsWith(expectedMessage));
  }

}