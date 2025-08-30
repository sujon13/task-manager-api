package com.example.incident.service;

import com.example.exception.NotFoundException;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.model.*;
import com.example.incident.repository.IncidentRepository;
import com.example.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final AffectedEquipmentService affectedEquipmentService;
    private final ActionsTakenService actionsTakenService;
    private final IncidentUtil incidentUtil;
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

    private void addActionsTaken(IncidentResponse incidentResponse, Incident incident, List<ActionsTaken> actionsTakenList) {
        Map<String, List<ActionTakenRequest>> actionsTakenMap =
                actionsTakenList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ActionsTaken::getTaker,
                                        Collectors.mapping(
                                                actionsTakenService::buildActionTakenRequest ,
                                                Collectors.toList()
                                        )
                                )
                        );

        incidentResponse.setActionsTakenByScada(
                actionsTakenMap.getOrDefault(incident.getReportedBy(), List.of())
        );
        incidentResponse.setActionsTakenByContractor(
                actionsTakenMap.getOrDefault(incident.getAssignedTo(), List.of())
        );
    }

    private IncidentResponse buildIncidentResponse(Incident incident,
                                                   List<String> affectedEquipments, List<ActionsTaken> actionsTakenList) {
        IncidentResponse incidentResponse = new IncidentResponse();
        BeanUtils.copyProperties(incident, incidentResponse);
        incidentResponse.setAffectedEquipments(affectedEquipments);
        addActionsTaken(incidentResponse, incident, actionsTakenList);
        return incidentResponse;
    }

    private IncidentResponse buildIncidentResponse(Incident incident) {
        List<String> affectedEquipments = affectedEquipmentService.findAllEquipmentNamesByIncidentId(incident.getId());
        List<ActionsTaken> actionsTakenList = actionsTakenService.findAllByIncidentId(incident.getId());

        return buildIncidentResponse(incident, affectedEquipments, actionsTakenList);
    }

    private List<IncidentResponse> buildIncidentResponses(List<Incident> incidents) {
        List<Integer> incidentIds = incidents.stream().map(Incident::getId).toList();
        Map<Integer, List<String>> affectedEquipmentMap = affectedEquipmentService.getAffectedEquipmentMap(incidentIds);
        Map<Integer, List<ActionsTaken>> actionsTakenMap = actionsTakenService.findIncidentIdToActionsTakenMap(incidentIds);

        return incidents.stream()
                .map(incident -> buildIncidentResponse(
                        incident,
                        affectedEquipmentMap.getOrDefault(incident.getId(), List.of()),
                        actionsTakenMap.getOrDefault(incident.getId(), List.of()))
                )
                .toList();
    }

    public List<IncidentResponse> getIncidents() {
        List<Incident> incidentList = incidentRepository.findAll();
        return buildIncidentResponses(incidentList);
    }

    public IncidentResponse getIncident(int id) {
        Incident incident = findById(id);
        return buildIncidentResponse(incident);
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

    private void addActionsTaken(Incident incident, IncidentRequest request) {
        actionsTakenService.addActionsTaken(incident.getId(), userUtil.getUserName(), request.getActionsTakenByScada());
    }

    @Transactional
    public IncidentResponse addIncident(IncidentRequest request) {
        Incident incident = createIncidentFromRequest(request);
        incidentRepository.save(incident);
        affectedEquipmentService.addAffectedEquipments(incident.getId(), request.getAffectedEquipments());
        addActionsTaken(incident, request);

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
        incidentUtil.checkEditPermission(incident);

        updateIncident(incident, request);
        affectedEquipmentService.updateAffectedEquipments(incident.getId(), request.getAffectedEquipments());
        return buildIncidentResponse(incident);
    }

    private void checkStatusEditPermission(Incident incident, IncidentStatus newStatus) {
        if (IncidentStatus.RESOLVED.equals(newStatus) && incidentUtil.isAssignee(incident)) {
            throw new AccessDeniedException("You do not have permission to resolve this incident");
        }
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(final int id, IncidentUpdateRequest updateRequest) {
        Incident incident = findById(id);
        incidentUtil.checkEditPermission(incident); // creator, admin or assignee
        checkStatusEditPermission(incident, updateRequest.getStatus()); // assignee can not resolve incident

        incident.setStatus(updateRequest.getStatus());
        return buildIncidentResponse(incident);
    }
}
