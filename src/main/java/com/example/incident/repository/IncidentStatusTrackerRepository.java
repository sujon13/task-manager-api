package com.example.incident.repository;

import com.example.incident.model.IncidentStatusTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentStatusTrackerRepository extends JpaRepository<IncidentStatusTracker, Integer> {
    List<IncidentStatusTracker> findAllByIncidentId(int incidentId);
}