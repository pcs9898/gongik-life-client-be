package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyPostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyPostsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsResponseDto;
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
class MyPostsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private MyPostsService myPostsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 사용하여 수동으로 주입합니다.
        ReflectionTestUtils.setField(myPostsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("내 게시글 조회 성공")
    void myPosts_Success() {
        // Given
        MyPostsRequestDto requestDto = createTestMyPostsRequestDto();
        MyPostsRequest protoRequest = requestDto.toMyPostsRequestProto();
        // Dummy gRPC 응답 객체 생성 (필요한 응답 필드를 설정)
        MyPostsResponse grpcResponse = MyPostsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder().setHasNextPage(true).setEndCursor("end-cursor").build())
                .build();

        when(communityServiceBlockingStub.myPosts(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When
        MyPostsResponseDto responseDto = myPostsService.myPosts(requestDto);

        // Then
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).myPosts(eq(protoRequest));
    }

    @Test
    @DisplayName("내 게시글 조회 gRPC 에러 발생 시 예외 처리")
    void myPosts_WhenGrpcError() {
        // Given
        MyPostsRequestDto requestDto = createTestMyPostsRequestDto();

        when(communityServiceBlockingStub.myPosts(any(MyPostsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                myPostsService.myPosts(requestDto));
        assertTrue(exception.getMessage().contains("MyPostsService"));
    }

    // 테스트용 MyPostsRequestDto 객체 생성 메서드
    private MyPostsRequestDto createTestMyPostsRequestDto() {
        return MyPostsRequestDto.builder()
                .userId("test-user-id")
                .pageSize(10)
                // 필요한 다른 필드가 있다면 추가 설정합니다.
                .build();
    }
}
