package org.example.gongiklifeclientbereportservice.repository;

import java.util.UUID;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {


}
