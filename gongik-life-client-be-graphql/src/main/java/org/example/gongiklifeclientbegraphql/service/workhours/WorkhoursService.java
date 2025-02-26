package org.example.gongiklifeclientbegraphql.service.workhours;

import com.gongik.workhoursService.domain.service.WorkhoursServiceGrpc;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.GetAverageWorkhoursResponse;
import dto.workhours.AverageWorkHoursRedisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.workhours.averageWorkhours.AverageWorkhoursResponseDto;
import org.example.gongiklifeclientbegraphql.service.institution.InstitutionService;
import org.example.gongiklifeclientbegraphql.service.user.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkhoursService {

    private static final String AVERAGE_WORKHOURS_CACHE_KEY = "averageWorkhours";

    private final UserService userService;
    private final InstitutionService institutionService;
    private final RedisTemplate<String, Object> redisTemplate;

    @GrpcClient("gongik-life-client-be-workhours-service")
    private WorkhoursServiceGrpc.WorkhoursServiceBlockingStub workhoursServiceBlockingStub;

    /**
     * 평균 근무시간 정보를 조회하여 반환합니다.
     *
     * @param userId 사용자 ID
     * @return AverageWorkhoursResponseDto 평균 근무시간 응답 DTO
     */
    public AverageWorkhoursResponseDto averageWorkhours(String userId) {
        // 1. 사용자와 기관 정보를 통해 본인의 평균 근무시간 조회
        String userInstitutionId = userService.hasInstitution(userId);
        Integer myAverageWorkhours = institutionService.getMyAverageWorkhours(userId, userInstitutionId);

        // 2. Redis 캐시에서 평균 근무시간 데이터를 조회
        AverageWorkHoursRedisDto cachedData = getCachedAverageWorkhours();
        if (cachedData != null) {
            return buildResponseFromCache(myAverageWorkhours, cachedData);
        }

        // 3. gRPC 호출을 통해 평균 근무시간 데이터를 가져오고 응답 생성
        GetAverageWorkhoursResponse grpcResponse = fetchAverageWorkhoursFromGrpc();
        return buildResponseFromGrpc(myAverageWorkhours, grpcResponse);
    }

    /**
     * Redis 캐시에서 평균 근무시간 데이터를 조회합니다.
     *
     * @return AverageWorkHoursRedisDto 캐시된 데이터 또는 null
     */
    private AverageWorkHoursRedisDto getCachedAverageWorkhours() {
        Object cachedValue = redisTemplate.opsForValue().get(AVERAGE_WORKHOURS_CACHE_KEY);
        if (cachedValue instanceof AverageWorkHoursRedisDto) {
            log.info("Cache hit for average work hours");
            return (AverageWorkHoursRedisDto) cachedValue;
        }
        log.info("Cache miss for average work hours");
        return null;
    }

    /**
     * gRPC 호출을 통해 평균 근무시간 데이터를 가져옵니다.
     *
     * @return GetAverageWorkhoursResponse gRPC 서버 응답
     */
    private GetAverageWorkhoursResponse fetchAverageWorkhoursFromGrpc() {
        try {
            log.info("Fetching average work hours from gRPC server...");
            return workhoursServiceBlockingStub.getAverageWorkhours(Empty.newBuilder().build());
        } catch (Exception e) {
            log.error("Failed to fetch average work hours from gRPC server", e);
            throw new RuntimeException("Failed to fetch average work hours", e);
        }
    }

    /**
     * Redis 캐시 데이터를 기반으로 응답 DTO를 생성합니다.
     *
     * @param myAverageWorkhours 사용자 본인의 평균 근무시간
     * @param cachedData         Redis 캐시에 저장된 데이터
     * @return AverageWorkhoursResponseDto 응답 DTO
     */
    private AverageWorkhoursResponseDto buildResponseFromCache(Integer myAverageWorkhours,
                                                               AverageWorkHoursRedisDto cachedData) {
        return AverageWorkhoursResponseDto.builder()
                .myAverageWorkhours(myAverageWorkhours)
                .socialWelfareWorkhours(cachedData.getSocialWelfareWorkhours())
                .publicOrganizationWorkhours(cachedData.getPublicOrganizationWorkhours())
                .nationalAgencyWorkhours(cachedData.getNationalAgencyWorkhours())
                .localGovernmentWorkhours(cachedData.getLocalGovernmentWorkhours())
                .totalVoteCount(cachedData.getTotalVoteCount())
                .build();
    }

    /**
     * gRPC 서버 데이터를 기반으로 응답 DTO를 생성합니다.
     *
     * @param myAverageWorkhours 사용자 본인의 평균 근무시간
     * @param grpcResponse       gRPC 서버에서 가져온 데이터
     * @return AverageWorkhoursResponseDto 응답 DTO
     */
    private AverageWorkhoursResponseDto buildResponseFromGrpc(Integer myAverageWorkhours,
                                                              GetAverageWorkhoursResponse grpcResponse) {
        return AverageWorkhoursResponseDto.builder()
                .myAverageWorkhours(myAverageWorkhours)
                .socialWelfareWorkhours(grpcResponse.getSocialWelfareWorkhours())
                .publicOrganizationWorkhours(grpcResponse.getPublicOrganizationWorkhours())
                .nationalAgencyWorkhours(grpcResponse.getNationalAgencyWorkhours())
                .localGovernmentWorkhours(grpcResponse.getLocalGovernmentWorkhours())
                .totalVoteCount(grpcResponse.getTotalVoteCount())
                .build();
    }
}
