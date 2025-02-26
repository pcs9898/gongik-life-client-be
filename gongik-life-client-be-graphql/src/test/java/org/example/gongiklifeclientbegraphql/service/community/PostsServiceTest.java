package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsResponseDto;
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
class PostsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private PostsService postsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 강제로 주입하여 테스트 환경을 구성합니다.
        ReflectionTestUtils.setField(postsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void posts_Success() {
        // Given: 테스트용 PostsRequestDto 생성 및 proto 변환
        PostsRequestDto requestDto = createTestPostsRequestDto();
        PostsRequest protoRequest = requestDto.toPostsRequestProto();
        // Dummy gRPC 응답 객체 생성 (필요한 응답 필드를 설정)
        PostsResponse grpcResponse = PostsResponse.newBuilder()
                // 예: 게시글 총 개수, 혹은 리스트 등 필요한 필드들 설정
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder().setEndCursor("end-cursor").setHasNextPage(true).build())
                .build();

        when(communityServiceBlockingStub.posts(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When: 게시글 목록 조회 서비스 메서드 호출
        PostsResponseDto responseDto = postsService.posts(requestDto);

        // Then: 응답 DTO가 null이 아님을 확인하고, stub의 posts() 메서드가 올바른 인자로 호출되었음을 검증합니다.
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).posts(eq(protoRequest));
    }

    @Test
    @DisplayName("게시글 목록 조회 gRPC 에러 발생 시 예외 처리")
    void posts_WhenGrpcError() {
        // Given: 테스트용 PostsRequestDto 생성
        PostsRequestDto requestDto = createTestPostsRequestDto();

        when(communityServiceBlockingStub.posts(any(PostsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: gRPC 호출 중 예외 발생 시 "PostsService" 키워드가 포함된 RuntimeException이 던져지는지 확인
        RuntimeException exception = assertThrows(RuntimeException.class, () -> postsService.posts(requestDto));
        assertTrue(exception.getMessage().contains("PostsService"));
    }

    // 테스트용 PostsRequestDto 객체 생성 메서드 (필요한 필드를 채워서 생성)
    private PostsRequestDto createTestPostsRequestDto() {
        return PostsRequestDto.builder()
                // 예: userId, page, pageSize 등 필요한 필드를 설정
                .postCategoryId(1)
                .userId("test-user-id")
                .pageSize(10)
                .build();
    }
}
