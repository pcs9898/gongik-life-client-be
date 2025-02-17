package org.example.gongiklifeclientbeworkhoursservice.service;

import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.Empty;
import com.gongik.workhoursService.domain.service.WorkhoursServiceOuterClass.GetAverageWorkhoursResponse;
import dto.workhours.AverageWorkHoursRedisDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbeworkhoursservice.entity.WorkhoursStatistic;
import org.example.gongiklifeclientbeworkhoursservice.repository.WorkhoursStatisticRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkhoursService {

  private final WorkhoursStatisticRepository workhoursStatisticRepository;
  private final RedisTemplate<String, Object> redisTemplate;


  public GetAverageWorkhoursResponse getAverageWorkhours(Empty request) {
    WorkhoursStatistic workhoursStatistic = workhoursStatisticRepository.findTopByOrderByCreatedAtDesc();

    redisTemplate.opsForValue().set("averageWorkhours", AverageWorkHoursRedisDto.builder()
        .socialWelfareWorkhours(workhoursStatistic.getSocialWelfareWorkhours())
        .publicOrganizationWorkhours(workhoursStatistic.getPublicOrganizationWorkhours())
        .nationalAgencyWorkhours(workhoursStatistic.getNationalAgencyWorkhours())
        .localGovernmentWorkhours(workhoursStatistic.getLocalGovernmentWorkhours())
        .totalVoteCount(workhoursStatistic.getTotalVoteCount()).build(), Duration.ofDays(7)
    );

    return GetAverageWorkhoursResponse.newBuilder()
        .setSocialWelfareWorkhours(workhoursStatistic.getSocialWelfareWorkhours())
        .setPublicOrganizationWorkhours(workhoursStatistic.getPublicOrganizationWorkhours())
        .setNationalAgencyWorkhours(workhoursStatistic.getNationalAgencyWorkhours())
        .setLocalGovernmentWorkhours(workhoursStatistic.getLocalGovernmentWorkhours())
        .setTotalVoteCount(workhoursStatistic.getTotalVoteCount())
        .build();
  }
}
