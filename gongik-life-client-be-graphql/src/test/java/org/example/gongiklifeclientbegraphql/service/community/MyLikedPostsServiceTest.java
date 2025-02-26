package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsResponseDto;
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
class MyLikedPostsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private MyLikedPostsService myLikedPostsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동 주입
        ReflectionTestUtils.setField(myLikedPostsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("내 좋아요 게시글 조회 성공")
    void myLikedPosts_Success() {
        // Given: 테스트용 요청 DTO 생성 및 Proto 메시지 변환
        MyLikedPostsRequestDto requestDto = createTestMyLikedPostsRequestDto();
        MyLikedPostsRequest protoRequest = requestDto.toMyLikedPostsRequestProto();
        // Dummy gRPC 응답 생성 (필요한 응답 필드를 설정)
        MyLikedPostsResponse grpcResponse = MyLikedPostsResponse.newBuilder()
                // 예: .setTotalCount(10) 등 필요한 필드 설정 가능
                .build();

        when(communityServiceBlockingStub.myLikedPosts(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When: 서비스 메서드 호출
        MyLikedPostsResponseDto responseDto = myLikedPostsService.myLikedPosts(requestDto);

        // Then: 반환된 결과가 null이 아니며, stub 호출이 올바른 인자와 함께 발생했음을 검증
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).myLikedPosts(eq(protoRequest));
    }

    @Test
    @DisplayName("내 좋아요 게시글 조회 gRPC 에러 발생 시 예외 처리")
    void myLikedPosts_WhenGrpcError() {
        // Given: 테스트용 요청 DTO 생성
        MyLikedPostsRequestDto requestDto = createTestMyLikedPostsRequestDto();

        when(communityServiceBlockingStub.myLikedPosts(any(MyLikedPostsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: 예외 발생 및 예외 메시지에 "MyLikedPostsService" 포함 여부 확인
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                myLikedPostsService.myLikedPosts(requestDto));
        assertTrue(exception.getMessage().contains("MyLikedPostsService"));
    }

    // 테스트용 MyLikedPostsRequestDto 객체 생성 메서드
    private MyLikedPostsRequestDto createTestMyLikedPostsRequestDto() {
        // 필요한 필드를 채워 테스트용 DTO 객체를 생성 (예: userId, 등)
        return MyLikedPostsRequestDto.builder()
                .userId("test-user-id")
                .pageSize(10)
                // 추가 필드가 있을 경우 설정
                .build();
    }
}
