package com.example.incident.repository;

import com.example.incident.model.AffectedEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AffectedEquipmentRepository extends JpaRepository<AffectedEquipment, Integer> {
    List<AffectedEquipment> findAllByIncidentId(int incidentId);

    void deleteAllByIncidentId(int incidentId);

    List<AffectedEquipment> findAllByIncidentIdIn(List<Integer> incidentIds);
}