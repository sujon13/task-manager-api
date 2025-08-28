package com.example.incident.service;

import com.example.incident.model.AffectedEquipment;
import com.example.incident.repository.AffectedEquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AffectedEquipmentService {
    private final AffectedEquipmentRepository equipmentRepository;

    public List<AffectedEquipment> findAllByIncidentId(int incidentId) {
        return equipmentRepository.findAllByIncidentId(incidentId);
    }

    public List<String> findAllEquipmentNamesByIncidentId(int incidentId) {
        return equipmentRepository.findAllByIncidentId(incidentId).stream()
                .map(AffectedEquipment::getEquipment)
                .toList();
    }

    public Map<Integer, List<String>> getAffectedEquipmentMap(List<Integer> incidentIdList) {
        return findAllByIncidentIdList(incidentIdList)
                .stream().collect(
                        Collectors.groupingBy(
                                AffectedEquipment::getIncidentId,
                                Collectors.mapping(AffectedEquipment::getEquipment, Collectors.toList()))
                );
    }

    public List<AffectedEquipment> findAllByIncidentIdList(List<Integer> incidentIds) {
        return equipmentRepository.findAllByIncidentIdIn(incidentIds);
    }

    @Transactional
    public void deleteByIncidentId(int incidentId) {
        equipmentRepository.deleteAllByIncidentId(incidentId);
    }

    private AffectedEquipment buildAffectedEquipment(int incidentId, String equipment) {
        AffectedEquipment affectedEquipment = new AffectedEquipment();
        affectedEquipment.setIncidentId(incidentId);
        affectedEquipment.setEquipment(equipment);
        return affectedEquipment;
    }

    private List<AffectedEquipment> buildAffectedEquipments(int incidentId, List<String> equipments) {
        return equipments.stream()
                .map(equipment -> buildAffectedEquipment(incidentId, equipment))
                .toList();
    }

    @Transactional
    public void addAffectedEquipments(int incidentId, List<String> equipments) {
        List<AffectedEquipment> affectedEquipments = buildAffectedEquipments(incidentId, equipments);
        equipmentRepository.saveAll(affectedEquipments);
    }

    @Transactional
    public void updateAffectedEquipments(int incidentId, List<String> equipments) {
        deleteByIncidentId(incidentId);
        addAffectedEquipments(incidentId, equipments);
    }

}
