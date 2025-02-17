package org.example.gongiklifeclientbegraphql.service;

import com.gongik.workhoursService.domain.service.WorkhoursServiceGrpc;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.GetAverageWorkhoursResponse;
import dto.workhours.AverageWorkHoursRedisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.workhours.averageWorkhours.AverageWorkhoursResponseDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkhoursService {

  private final UserService userService;
  private final InstitutionService institutionService;
  private final RedisTemplate<String, Object> redisTemplate;

  @GrpcClient("gongik-life-client-be-workhours-service")
  private WorkhoursServiceGrpc.WorkhoursServiceBlockingStub workhoursServiceBlockingStub;

  public AverageWorkhoursResponseDto averageWorkhours(String userId) {
    String userInstitutionId = userService.hasInstitution(userId);

    Integer myAverageWorkhours = institutionService.getMyAverageWorkhours(userId,
        userInstitutionId);

    AverageWorkHoursRedisDto averageWorkHoursRedisDto = (AverageWorkHoursRedisDto) redisTemplate.opsForValue()
        .get("averageWorkhours");

    if (averageWorkHoursRedisDto != null) {
      return AverageWorkhoursResponseDto.builder()
          .myAverageWorkhours(myAverageWorkhours)
          .socialWelfareWorkhours(averageWorkHoursRedisDto.getSocialWelfareWorkhours())
          .publicOrganizationWorkhours(averageWorkHoursRedisDto.getPublicOrganizationWorkhours())
          .nationalAgencyWorkhours(averageWorkHoursRedisDto.getNationalAgencyWorkhours())
          .localGovernmentWorkhours(averageWorkHoursRedisDto.getLocalGovernmentWorkhours())
          .totalVoteCount(averageWorkHoursRedisDto.getTotalVoteCount())
          .build();
    } else {
      try {
        GetAverageWorkhoursResponse averageWorkHours = workhoursServiceBlockingStub
            .getAverageWorkhours(Empty.newBuilder().build());

        return AverageWorkhoursResponseDto.builder()
            .myAverageWorkhours(myAverageWorkhours)
            .socialWelfareWorkhours(averageWorkHours.getSocialWelfareWorkhours())
            .publicOrganizationWorkhours(averageWorkHours.getPublicOrganizationWorkhours())
            .nationalAgencyWorkhours(averageWorkHours.getNationalAgencyWorkhours())
            .localGovernmentWorkhours(averageWorkHours.getLocalGovernmentWorkhours())
            .totalVoteCount(averageWorkHours.getTotalVoteCount())
            .build();
      } catch (Exception e) {
        log.error("Failt to get average workhours", e);
        throw e;
      }
    }
  }


}
