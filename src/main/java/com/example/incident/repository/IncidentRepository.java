package com.example.incident.repository;

import com.example.incident.enums.IncidentStatus;
import com.example.incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    List<Incident> findAllByReportedBy(String reportedBy);
    List<Incident> findAllByReportedByAndStatus(String reportedBy, IncidentStatus status);
    List<Incident> findAllByStatus(IncidentStatus status);
    List<Incident> findAllByReportedAtBetween(LocalDateTime start, LocalDateTime end);
}