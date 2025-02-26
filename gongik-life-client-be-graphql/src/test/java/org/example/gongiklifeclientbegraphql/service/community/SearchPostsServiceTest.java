package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.SearchPostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.SearchPostsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPostsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private SearchPostsService searchPostsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드는 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(searchPostsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("게시글 검색 성공")
    void searchPosts_Success() {
        // Given: 테스트용 SearchPostsRequestDto 생성 및 Proto 메시지 변환
        SearchPostsRequestDto requestDto = createTestSearchPostsRequestDto();
        SearchPostsRequest protoRequest = requestDto.toSearchPostsRequestProto();
        // Dummy gRPC 응답 객체 생성 (필요한 응답 필드를 설정)
        SearchPostsResponse grpcResponse = SearchPostsResponse.newBuilder()
                // 예시: 조회된 게시글 총 개수나 목록 등 필요한 필드 설정
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder().setHasNextPage(true).setEndCursor("end-cursor").build())
                .build();

        when(communityServiceBlockingStub.searchPosts(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When: 서비스 메서드 호출
        SearchPostsResponseDto responseDto = searchPostsService.searchPosts(requestDto);

        // Then: 응답 DTO가 null이 아니며, stub의 searchPosts() 메서드가 올바른 인자와 함께 호출되었음을 검증
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).searchPosts(eq(protoRequest));
    }

    @Test
    @DisplayName("게시글 검색 gRPC 에러 발생 시 예외 처리")
    void searchPosts_WhenGrpcError() {
        // Given: 테스트용 SearchPostsRequestDto 생성
        SearchPostsRequestDto requestDto = createTestSearchPostsRequestDto();

        when(communityServiceBlockingStub.searchPosts(any(SearchPostsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: gRPC 호출 중 예외 발생 시 "SearchPostsService" 키워드가 포함된 RuntimeException이 던져지는지 확인
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                searchPostsService.searchPosts(requestDto)
        );
        assertTrue(exception.getMessage().contains("SearchPostsService"));
    }

    // 테스트용 SearchPostsRequestDto 객체 생성 메서드
    private SearchPostsRequestDto createTestSearchPostsRequestDto() {
        return SearchPostsRequestDto.builder()
                .searchKeyword("test-keyword")
                .postCategoryId(7)
                .pageSize(10)
                // 필요한 추가 필드가 있다면 설정
                .build();
    }
}
