package org.example.gongiklifeclientbeworkhoursservice.repository;

import java.time.LocalDate;
import java.util.UUID;
import org.example.gongiklifeclientbeworkhoursservice.entity.WorkhoursStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkhoursStatisticRepository extends JpaRepository<WorkhoursStatistic, UUID> {

  boolean existsByStatisticsDate(LocalDate statisticsDate);

  WorkhoursStatistic findTopByOrderByCreatedAtDesc();

}