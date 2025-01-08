package org.example.gongiklifeclientbeinstitutionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "institution_reviews")
public class InstitutionReview {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "institution_id", nullable = false)
  private Institution institution;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private Double rating;

  @Column(name = "facility_rating")
  private Double facilityRating;

  @Column(name = "location_rating")
  private Double locationRating;

  @Column(name = "staff_rating")
  private Double staffRating;

  @Column(name = "visitor_rating")
  private Double visitorRating;

  @Column(name = "vacation_freedom_rating")
  private Double vacationFreedomRating;

  @Column(name = "main_tasks")
  private String mainTasks;

  @Column(name = "pros_cons")
  private String prosCons;

  @Column(name = "average_workhours")
  private Integer averageWorkhours;

  @ManyToOne
  @JoinColumn(name = "work_type_rules_id", nullable = false)
  private WorkTypeRule workTypeRule;

  @ManyToOne
  @JoinColumn(name = "uniform_wearing_rules_id", nullable = false)
  private UniformWearingRule uniformWearingRule;

  @ManyToOne
  @JoinColumn(name = "social_service_people_count_id", nullable = false)
  private SocialServicePeopleCount socialServicePeopleCount;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;
}