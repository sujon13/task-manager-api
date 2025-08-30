package com.example.incident.repository;

import com.example.incident.model.ActionsTaken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActionsTakenRepository extends JpaRepository<ActionsTaken, Integer> {
    List<ActionsTaken> findAllByIncidentIdAndTaker(int incidentId, String taker);

    void deleteAllByIncidentId(int incidentId);

    List<ActionsTaken> findAllByIncidentId(int incidentId);

    List<ActionsTaken> findAllByIncidentIdIn(List<Integer> incidentIdList);
}