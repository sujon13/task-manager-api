package com.example.incident.service;

import com.example.exception.NotFoundException;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.model.Incident;
import com.example.incident.model.IncidentRequest;
import com.example.incident.model.IncidentResponse;
import com.example.incident.repository.IncidentRepository;
import com.example.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final AffectedEquipmentService affectedEquipmentService;
    private final UserUtil userUtil;


    public List<Incident> findAllByIds(List<Integer> ids) {
        return incidentRepository.findAllById(ids);
    }

    public Incident findById(int id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incident not found with id " + id));
    }

    @Transactional
    public void deleteIncident(int id) {
        incidentRepository.deleteById(id);
    }

    private IncidentResponse buildIncidentResponse(Incident incident) {
        IncidentResponse incidentResponse = new IncidentResponse();
        BeanUtils.copyProperties(incident, incidentResponse);
        incidentResponse.setAffectedEquipments(
               affectedEquipmentService.findAllEquipmentNamesByIncidentId(incident.getId())
        );
        return incidentResponse;
    }

    private List<IncidentResponse> buildIncidentResponses(List<Incident> incidents) {
        return incidents.stream()
                .map(this::buildIncidentResponse)
                .toList();
    }

    private void addAffectedEquipmentsToResponses(List<IncidentResponse> incidentResponses) {
        List<Integer> incidentIds = incidentResponses.stream()
                 .map(IncidentResponse::getId)
                 .toList();
         Map<Integer, List<String>> affectedEquipmentMap = affectedEquipmentService.getAffectedEquipmentMap(incidentIds);
         incidentResponses.forEach(incidentResponse -> {
             incidentResponse.setAffectedEquipments(
                     affectedEquipmentMap.getOrDefault(incidentResponse.getId(), List.of())
             );
         });
    }

    public List<IncidentResponse> getIncidents() {
        List<Incident> incidentList = incidentRepository.findAll();
        List<IncidentResponse> incidentResponses = buildIncidentResponses(incidentList);
        addAffectedEquipmentsToResponses(incidentResponses);
        return incidentResponses;
    }

    public IncidentResponse getIncident(int id) {
        Incident incident = findById(id);
        IncidentResponse incidentResponse = buildIncidentResponse(incident);
        incidentResponse.setAffectedEquipments(
                affectedEquipmentService.findAllEquipmentNamesByIncidentId(incident.getId())
        );
        return incidentResponse;
    }

    private int getTotalEvent() {
        return (int)incidentRepository.count();
    }

    private Incident createIncidentFromRequest(IncidentRequest request) {
        Incident incident = new Incident();
        BeanUtils.copyProperties(request, incident);
        incident.setEventNo(getTotalEvent() + 1);
        incident.setReportedBy(userUtil.getUserName());
        return incident;
    }

    @Transactional
    public IncidentResponse addIncident(IncidentRequest request) {
        Incident incident = createIncidentFromRequest(request);
        affectedEquipmentService.addAffectedEquipments(incident.getId(), request.getAffectedEquipments());
        incidentRepository.save(incident);
        return buildIncidentResponse(incident);
    }

    private void updateIncident(Incident incident, IncidentRequest request) {
        if (request.getStation() != null)
            incident.setStation(request.getStation());
        if (request.getLocation() != null)
            incident.setLocation(request.getLocation());
        if (request.getOccurredAt() != null)
            incident.setOccurredAt(request.getOccurredAt());
        // skip reportedAt
        if (request.getAssignedTo() != null)
            incident.setAssignedTo(request.getAssignedTo());
        // skip resolvedAt
        if (request.getFaultNature() != null)
            incident.setFaultNature(request.getFaultNature());
        if (request.getSummary() != null)
            incident.setSummary(request.getSummary());
        if (request.getDescription() != null)
            incident.setDescription(request.getDescription());
        if (request.getRemarksByScada() != null)
            incident.setRemarksByScada(request.getRemarksByScada());
        if (request.getRemarksByContractor() != null)
            incident.setRemarksByContractor(request.getRemarksByContractor());
    }

    @Transactional
    public IncidentResponse updateIncident(final int id, IncidentRequest request) {
        Incident incident = findById(id);
        updateIncident(incident, request);
        affectedEquipmentService.updateAffectedEquipments(incident.getId(), request.getAffectedEquipments());
        return buildIncidentResponse(incident);
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(final int id, IncidentStatus status) {
        Incident incident = findById(id);

        // here business logic to update incident status
        // incident.setStatus(status);
        return buildIncidentResponse(incident);
    }
}
