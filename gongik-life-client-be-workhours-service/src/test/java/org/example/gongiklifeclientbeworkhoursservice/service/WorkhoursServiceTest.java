package org.example.gongiklifeclientbeworkhoursservice.service;

import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass;
import dto.workhours.AverageWorkHoursRedisDto;
import org.example.gongiklifeclientbeworkhoursservice.entity.WorkhoursStatistic;
import org.example.gongiklifeclientbeworkhoursservice.repository.WorkhoursStatisticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkhoursServiceTest {

    private static final String CACHE_KEY = "averageWorkhours";

    @Mock
    private WorkhoursStatisticRepository workhoursStatisticRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private WorkhoursService workhoursService;

    @Test
    @DisplayName("getAverageWorkhours: 정상 동작 - 캐시 설정 및 응답 DTO 생성")
    void testGetAverageWorkhours_success() {
        // Arrange
        // 더미 WorkhoursStatistic 생성
        WorkhoursStatistic dummyStatistic = new WorkhoursStatistic();
        dummyStatistic.setSocialWelfareWorkhours(10);
        dummyStatistic.setPublicOrganizationWorkhours(20);
        dummyStatistic.setNationalAgencyWorkhours(30);
        dummyStatistic.setLocalGovernmentWorkhours(40);
        dummyStatistic.setTotalVoteCount(100);
        dummyStatistic.setCreatedAt(LocalDateTime.now()); // Auditing 필드 등 채워짐

        // Repository가 더미 데이터를 반환하도록 설정
        when(workhoursStatisticRepository.findTopByOrderByCreatedAtDesc()).thenReturn(dummyStatistic);
        // RedisTemplate의 opsForValue() 메서드 동작 모킹
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Expected: WorkhoursService는 dummyStatistic을 바탕으로 AverageWorkHoursRedisDto를 생성하여 캐시에 저장
        AverageWorkHoursRedisDto expectedCacheDto = AverageWorkHoursRedisDto.builder()
                .socialWelfareWorkhours(dummyStatistic.getSocialWelfareWorkhours())
                .publicOrganizationWorkhours(dummyStatistic.getPublicOrganizationWorkhours())
                .nationalAgencyWorkhours(dummyStatistic.getNationalAgencyWorkhours())
                .localGovernmentWorkhours(dummyStatistic.getLocalGovernmentWorkhours())
                .totalVoteCount(dummyStatistic.getTotalVoteCount())
                .build();

        // Act
        WorkhoursServiceOuterClass.GetAverageWorkhoursResponse response = workhoursService.getAverageWorkhours(WorkhoursServiceOuterClass.Empty.newBuilder().build());

        // Assert: 응답 DTO가 Repository의 값을 그대로 반영하는지 검증
        assertNotNull(response, "응답은 null이 아니어야 합니다.");
        assertEquals(dummyStatistic.getSocialWelfareWorkhours(), response.getSocialWelfareWorkhours());
        assertEquals(dummyStatistic.getPublicOrganizationWorkhours(), response.getPublicOrganizationWorkhours());
        assertEquals(dummyStatistic.getNationalAgencyWorkhours(), response.getNationalAgencyWorkhours());
        assertEquals(dummyStatistic.getLocalGovernmentWorkhours(), response.getLocalGovernmentWorkhours());
        assertEquals(dummyStatistic.getTotalVoteCount(), response.getTotalVoteCount());

        // 캐시 저장이 호출되었는지 검증 (유효기간은 7일)
        verify(valueOperations).set(eq(CACHE_KEY), eq(expectedCacheDto), eq(Duration.ofDays(7)));
    }
}
