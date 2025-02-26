package org.example.gongiklifeclientbegraphql.service.workhours;

import com.gongik.workhoursService.domain.service.WorkhoursServiceGrpc;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass;
import dto.workhours.AverageWorkHoursRedisDto;
import org.example.gongiklifeclientbegraphql.dto.workhours.averageWorkhours.AverageWorkhoursResponseDto;
import org.example.gongiklifeclientbegraphql.service.institution.InstitutionService;
import org.example.gongiklifeclientbegraphql.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkhoursServiceTest {

    private static final String USER_ID = "11111111-1111-1111-1111-111111111111";
    private static final String AVERAGE_WORKHOURS_CACHE_KEY = "averageWorkhours";

    @Mock
    private UserService userService;

    @Mock
    private InstitutionService institutionService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOps;

    @Mock
    private WorkhoursServiceGrpc.WorkhoursServiceBlockingStub workhoursServiceBlockingStub;

    @InjectMocks
    private WorkhoursService workhoursService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(workhoursService, "workhoursServiceBlockingStub", workhoursServiceBlockingStub);
    }

    @Test
    @DisplayName("캐시가 존재할 때: Redis 캐시 데이터를 이용하여 응답 DTO 생성")
    void testAverageWorkhoursWithCache() {
        // Arrange
        String institutionId = "inst123";
        int myAverageWorkhours = 40;
        AverageWorkHoursRedisDto cachedDto = AverageWorkHoursRedisDto.builder()
                .socialWelfareWorkhours(10)
                .publicOrganizationWorkhours(20)
                .nationalAgencyWorkhours(30)
                .localGovernmentWorkhours(40)
                .totalVoteCount(100)
                .build();

        when(userService.hasInstitution(USER_ID)).thenReturn(institutionId);
        when(institutionService.getMyAverageWorkhours(USER_ID, institutionId)).thenReturn(myAverageWorkhours);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(eq(AVERAGE_WORKHOURS_CACHE_KEY))).thenReturn(cachedDto);

        // Act
        AverageWorkhoursResponseDto response = workhoursService.averageWorkhours(USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(myAverageWorkhours, response.getMyAverageWorkhours());
        assertEquals(cachedDto.getSocialWelfareWorkhours(), response.getSocialWelfareWorkhours());
        assertEquals(cachedDto.getPublicOrganizationWorkhours(), response.getPublicOrganizationWorkhours());
        assertEquals(cachedDto.getNationalAgencyWorkhours(), response.getNationalAgencyWorkhours());
        assertEquals(cachedDto.getLocalGovernmentWorkhours(), response.getLocalGovernmentWorkhours());
        assertEquals(cachedDto.getTotalVoteCount(), response.getTotalVoteCount());

        verify(userService).hasInstitution(USER_ID);
        verify(institutionService).getMyAverageWorkhours(USER_ID, institutionId);
        verify(redisTemplate.opsForValue()).get(eq(AVERAGE_WORKHOURS_CACHE_KEY));
    }

    @Test
    @DisplayName("캐시 미존재시: gRPC 호출을 통해 응답 DTO 생성")
    void testAverageWorkhoursWithGrpc() {
        // Arrange
        String institutionId = "inst123";
        int myAverageWorkhours = 40;
        when(userService.hasInstitution(USER_ID)).thenReturn(institutionId);
        when(institutionService.getMyAverageWorkhours(USER_ID, institutionId)).thenReturn(myAverageWorkhours);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        // Redis 캐시에 아무것도 없을 경우
        when(valueOps.get(eq(AVERAGE_WORKHOURS_CACHE_KEY))).thenReturn(null);

        // dummy gRPC 응답 생성
        WorkhoursServiceOuterClass.GetAverageWorkhoursResponse grpcResponse = WorkhoursServiceOuterClass.GetAverageWorkhoursResponse.newBuilder()
                .setSocialWelfareWorkhours(11)
                .setPublicOrganizationWorkhours(21)
                .setNationalAgencyWorkhours(31)
                .setLocalGovernmentWorkhours(41)
                .setTotalVoteCount(105)
                .build();
        when(workhoursServiceBlockingStub.getAverageWorkhours(any(WorkhoursServiceOuterClass.Empty.class)))
                .thenReturn(grpcResponse);

        // Act
        AverageWorkhoursResponseDto response = workhoursService.averageWorkhours(USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(myAverageWorkhours, response.getMyAverageWorkhours());
        assertEquals(grpcResponse.getSocialWelfareWorkhours(), response.getSocialWelfareWorkhours());
        assertEquals(grpcResponse.getPublicOrganizationWorkhours(), response.getPublicOrganizationWorkhours());
        assertEquals(grpcResponse.getNationalAgencyWorkhours(), response.getNationalAgencyWorkhours());
        assertEquals(grpcResponse.getLocalGovernmentWorkhours(), response.getLocalGovernmentWorkhours());
        assertEquals(grpcResponse.getTotalVoteCount(), response.getTotalVoteCount());

        verify(userService).hasInstitution(USER_ID);
        verify(institutionService).getMyAverageWorkhours(USER_ID, institutionId);
        verify(redisTemplate.opsForValue()).get(eq(AVERAGE_WORKHOURS_CACHE_KEY));
        verify(workhoursServiceBlockingStub).getAverageWorkhours(any(com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty.class));
    }

    @Test
    @DisplayName("gRPC 호출 실패시: 예외 전파")
    void testAverageWorkhoursGrpcException() {
        // Arrange
        String institutionId = "inst123";
        int myAverageWorkhours = 40;
        when(userService.hasInstitution(USER_ID)).thenReturn(institutionId);
        when(institutionService.getMyAverageWorkhours(USER_ID, institutionId)).thenReturn(myAverageWorkhours);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(eq(AVERAGE_WORKHOURS_CACHE_KEY))).thenReturn(null);

        when(workhoursServiceBlockingStub.getAverageWorkhours(any(com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty.class)))
                .thenThrow(new RuntimeException("gRPC failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                workhoursService.averageWorkhours(USER_ID)
        );
        assertTrue(exception.getMessage().contains("Failed to fetch"));

        verify(userService).hasInstitution(USER_ID);
        verify(institutionService).getMyAverageWorkhours(USER_ID, institutionId);
        verify(redisTemplate.opsForValue()).get(eq(AVERAGE_WORKHOURS_CACHE_KEY));
        verify(workhoursServiceBlockingStub).getAverageWorkhours(any(com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty.class));
    }
}
