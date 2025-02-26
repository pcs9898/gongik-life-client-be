package org.example.gongiklifeclientbeworkhoursservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workhours_statistics")
public class WorkhoursStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "statistics_date", nullable = false)
    private LocalDate statisticsDate;

    @Column(name = "social_welfare_workhours", nullable = false)
    private Integer socialWelfareWorkhours;

    @Column(name = "public_organization_workhours", nullable = false)
    private Integer publicOrganizationWorkhours;

    @Column(name = "national_agency_workhours", nullable = false)
    private Integer nationalAgencyWorkhours;

    @Column(name = "local_government_workhours", nullable = false)
    private Integer localGovernmentWorkhours;

    @Column(name = "total_vote_count", nullable = false)
    private Integer totalVoteCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
