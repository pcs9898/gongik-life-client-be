package org.example.gongiklifeclientbeworkhoursservice.batch.tasklet;

import dto.workhours.AverageWorkHoursRedisDto;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeworkhoursservice.dto.InstitutionDto;
import org.example.gongiklifeclientbeworkhoursservice.entity.WorkhoursStatistic;
import org.example.gongiklifeclientbeworkhoursservice.grpc.client.InstitutionStatisticsGrpcAsyncClient;
import org.example.gongiklifeclientbeworkhoursservice.repository.WorkhoursStatisticRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationTasklet implements Tasklet {

  private final InstitutionStatisticsGrpcAsyncClient institutionStatisticsGrpcAsyncClient;
  private final WorkhoursStatisticRepository statisticRepository;
  private final RedisTemplate<String, Object> redisTemplate;


  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {

    if (statisticRepository.existsByStatisticsDate(LocalDate.now())) {
      log.info("Workhours statistics already exists for today, skipping batch job");
      return RepeatStatus.FINISHED;
    }

    List<InstitutionDto> institutions = institutionStatisticsGrpcAsyncClient.getInstitutionsForWorkhourStatistics();

    long totalReviewCount = 0;
    int socialSum = 0, socialCount = 0;
    int publicSum = 0, publicCount = 0;
    int nationalSum = 0, nationalCount = 0;
    int localSum = 0, localCount = 0;

    for (InstitutionDto inst : institutions) {
      totalReviewCount += inst.getReviewCount();
      switch (inst.getInstitutionCategoryId()) {
        case 1:
          socialSum += inst.getAverageWorkhours();
          socialCount++;
          break;
        case 2:
          publicSum += inst.getAverageWorkhours();
          publicCount++;
          break;
        case 3:
          nationalSum += inst.getAverageWorkhours();
          nationalCount++;
          break;
        case 4:
          localSum += inst.getAverageWorkhours();
          localCount++;
          break;
      }
    }

    int socialAvg = socialCount > 0 ? socialSum / socialCount : 0;
    int publicAvg = publicCount > 0 ? publicSum / publicCount : 0;
    int nationalAvg = nationalCount > 0 ? nationalSum / nationalCount : 0;
    int localAvg = localCount > 0 ? localSum / localCount : 0;

    WorkhoursStatistic statistic = new WorkhoursStatistic();
    statistic.setStatisticsDate(LocalDate.now());
    statistic.setSocialWelfareWorkhours(socialAvg);
    statistic.setPublicOrganizationWorkhours(publicAvg);
    statistic.setNationalAgencyWorkhours(nationalAvg);
    statistic.setLocalGovernmentWorkhours(localAvg);
    statistic.setTotalVoteCount((int) totalReviewCount);

    statisticRepository.save(statistic);

    redisTemplate.opsForValue().set("averageWorkhours", AverageWorkHoursRedisDto.builder()
        .socialWelfareWorkhours(socialAvg)
        .publicOrganizationWorkhours(publicAvg)
        .nationalAgencyWorkhours(nationalAvg)
        .localGovernmentWorkhours(localAvg)
        .totalVoteCount((int) totalReviewCount).build(), Duration.ofDays(7)
    );

    return RepeatStatus.FINISHED;
  }


}
