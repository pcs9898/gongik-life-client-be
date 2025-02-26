package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UserPostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UserPostsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsResponseDto;
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
class UserPostsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private UserPostsService userPostsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 강제로 주입합니다.
        ReflectionTestUtils.setField(userPostsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("내 게시글 조회 성공")
    void userPosts_Success() {
        // Given: 테스트용 UserPostsRequestDto 생성 및 Proto 메시지 변환
        UserPostsRequestDto requestDto = createTestUserPostsRequestDto();
        UserPostsRequest protoRequest = requestDto.toUserPostsRequestProto();
        // Dummy gRPC 응답 객체 생성 (필요한 응답 필드 설정)
        UserPostsResponse grpcResponse = UserPostsResponse.newBuilder()
                .setPageInfo(CommunityServiceOuterClass.PageInfo.newBuilder().setEndCursor("end-cursor").setHasNextPage(true).build())
                .build();

        when(communityServiceBlockingStub.userPosts(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When: service 메서드 호출
        UserPostsResponseDto responseDto = userPostsService.userPosts(requestDto);

        // Then: 반환된 DTO가 null이 아님을 확인하고, stub 메서드가 올바른 인자와 함께 호출되었는지 검증
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).userPosts(eq(protoRequest));
    }

    @Test
    @DisplayName("내 게시글 조회 gRPC 에러 발생 시 예외 처리")
    void userPosts_WhenGrpcError() {
        // Given: 테스트용 UserPostsRequestDto 생성
        UserPostsRequestDto requestDto = createTestUserPostsRequestDto();

        when(communityServiceBlockingStub.userPosts(any(UserPostsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: gRPC 호출 시 예외 발생 및 처리 검증 – 예외 메시지에 "UserPostsService" 식별자가 포함되어야 함
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userPostsService.userPosts(requestDto));
        assertTrue(exception.getMessage().contains("UserPostsService"));
    }

    // 테스트용 UserPostsRequestDto 객체 생성 메서드
    private UserPostsRequestDto createTestUserPostsRequestDto() {
        return UserPostsRequestDto.builder()
                .userId("test-user-id")
                .pageSize(10)
                // 필요한 추가 필드가 있다면 설정
                .build();
    }
}
