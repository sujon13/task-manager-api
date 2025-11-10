package com.example.incident.service;

import com.example.auth.model.UserResponse;
import com.example.auth.service.UserService;
import com.example.exception.NotFoundException;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.model.*;
import com.example.incident.repository.IncidentRepository;
import com.example.incident.specification.IncidentSpecification;
import com.example.util.Constants;
import com.example.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final AffectedEquipmentService affectedEquipmentService;
    private final ActionsTakenService actionsTakenService;
    private final IncidentStatusTrackerService incidentStatusTrackerService;
    private final IncidentUtil incidentUtil;
    private final UserUtil userUtil;
    private final UserService userService;


    public Incident findById(int id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incident not found with id " + id));
    }

    private void save(Incident incident) {
        incidentRepository.save(incident);
        incidentStatusTrackerService.addIncidentStatus(incident, null, incident.getStatus());
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

        incidentResponse.setActionsTakenByReporter(
                actionsTakenMap.getOrDefault(incident.getReportedBy(), List.of())
        );
        incidentResponse.setActionsTakenByAssignee(
                actionsTakenMap.getOrDefault(incident.getAssignedTo(), List.of())
        );
    }

    private IncidentResponse buildIncidentResponse(Incident incident,
                                                   List<String> affectedEquipments,
                                                   List<ActionsTaken> actionsTakenList,
                                                   Map<String, UserResponse> userNameToUserResponseMap) {
        IncidentResponse incidentResponse = new IncidentResponse();
        BeanUtils.copyProperties(incident, incidentResponse);
        incidentResponse.setStatusStr(incident.getStatus().getDisplayName());
        incidentResponse.setPriorityStr(incident.getPriority().getDisplayName());
        incidentResponse.setCategory(incident.getCategory());
        incidentResponse.setCategoryStr(
                incident.getCategory() != null
                        ? incident.getCategory().getDisplayName()
                        : null
        );
        incidentResponse.setDivision(incident.getDivision());

        setUserDetailsToIncidentResponse(incidentResponse, incident, userNameToUserResponseMap);

        incidentResponse.setAffectedEquipments(affectedEquipments);
        addActionsTaken(incidentResponse, incident, actionsTakenList);
        return incidentResponse;
    }

    private IncidentResponse buildIncidentResponse(Incident incident) {
        List<String> affectedEquipments = affectedEquipmentService.findAllEquipmentNamesByIncidentId(incident.getId());
        List<ActionsTaken> actionsTakenList = actionsTakenService.findAllByIncidentId(incident.getId());
        Map<String, UserResponse> userNameToUserResponseMap = getUserNameToResponseMap(List.of(incident));

        return buildIncidentResponse(incident, affectedEquipments, actionsTakenList, userNameToUserResponseMap);
    }

    private void checkAndSetIfLoggedInUserIsReporterOrAssignee(IncidentResponse incidentResponse, String me) {
        UserResponse reportedBy = incidentResponse.getReportedBy();
        UserResponse assignedTo = incidentResponse.getAssignedTo();
        if (reportedBy != null)
            incidentResponse.setReporter(me.equals(reportedBy.getUserName()));
        if (assignedTo != null)
            incidentResponse.setAssignee(me.equals(assignedTo.getUserName()));
    }

    private Map<String, UserResponse> getUserNameToResponseMap(List<Incident> incidents) {
        Set<String> userNames = incidents.stream()
                .flatMap(incident -> Stream.of(
                        incident.getReportedBy(),
                        incident.getAssignedTo(),
                        incident.getInitialAssignee(),
                        incident.getPendingTo()
                ))
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        List<UserResponse> userResponses = userService.fetchUsers(userNames);
        return userResponses.stream()
                .collect(Collectors.toMap(UserResponse::getUserName, Function.identity()));
    }

    private void setUserDetailsToIncidentResponse(IncidentResponse incidentResponse, Incident incident,
                                                  Map<String, UserResponse> userResponseMap) {
        final String reportedBy = incident.getReportedBy();
        incidentResponse.setReportedBy(
                userResponseMap.getOrDefault(
                        reportedBy,
                        StringUtils.hasText(reportedBy)
                                ? UserResponse.builder().userName(reportedBy).build()
                                : null
                )
        );

        final String assignedTo = incident.getAssignedTo();
        incidentResponse.setAssignedTo(
                userResponseMap.getOrDefault(
                        assignedTo,
                        StringUtils.hasText(assignedTo)
                                ? UserResponse.builder().userName(assignedTo).build()
                                : null
                )
        );

        final String initialAssignee = incident.getInitialAssignee();
        incidentResponse.setInitialAssignee(
                userResponseMap.getOrDefault(
                        initialAssignee,
                        StringUtils.hasText(initialAssignee)
                                ? UserResponse.builder().userName(initialAssignee).build()
                                : null
                )
        );

        final String pendingTo = incident.getPendingTo();
        incidentResponse.setPendingTo(
                userResponseMap.getOrDefault(
                        pendingTo,
                        StringUtils.hasText(pendingTo)
                                ? UserResponse.builder().userName(pendingTo).build()
                                : null
                )
        );
    }

    private List<IncidentResponse> makeSorted(List<IncidentResponse> incidentResponses) {
        final String me = userUtil.getUserName();
        final boolean isSeScada = userUtil.isSeScada();
        return incidentResponses.stream()
                .sorted(Comparator.comparing(response -> {
                    if (response.getPendingTo() == null) {
                        return true;
                    } else if (isSeScada) {
                        return !incidentUtil.isPendingToSuperVisor(response);
                    } else {
                        return !me.equals(response.getPendingTo().getUserName());
                    }
                }))
                .toList();
    }

    private Page<IncidentResponse> buildIncidentResponses(Page<Incident> incidents) {
        List<Integer> incidentIds = incidents.stream().map(Incident::getId).toList();
        Map<Integer, List<String>> affectedEquipmentMap = affectedEquipmentService.getAffectedEquipmentMap(incidentIds);
        Map<Integer, List<ActionsTaken>> actionsTakenMap = actionsTakenService.findIncidentIdToActionsTakenMap(incidentIds);
        Map<String, UserResponse> userNameToUserResponseMap = getUserNameToResponseMap(incidents.toList());

        final String me = userUtil.getUserName();
        List<IncidentResponse> incidentResponses = incidents.stream()
                .map(incident -> buildIncidentResponse(
                        incident,
                        affectedEquipmentMap.getOrDefault(incident.getId(), List.of()),
                        actionsTakenMap.getOrDefault(incident.getId(), List.of()),
                        userNameToUserResponseMap)
                )
                .peek(incidentResponse -> checkAndSetIfLoggedInUserIsReporterOrAssignee(incidentResponse, me))
                .toList();
        return new PageImpl<>(makeSorted(incidentResponses), incidents.getPageable(), incidents.getTotalElements());
    }

    public Page<IncidentResponse> getIncidents(IncidentFilterRequest request, Pageable pageable) {
        if (request.getPendingToList().contains(Constants.SCADA_SE_USER_NAME)) {
            request.setPendingToList(List.of(Constants.SMD_XEN_USER_NAME, Constants.CNST_XEN_USER_NAME));
        }
        Specification<Incident> incidentSpecification = IncidentSpecification.buildSpecification(request);
        Page<Incident> incidents = incidentRepository.findAll(incidentSpecification, pageable);
        return buildIncidentResponses(incidents);
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

        incident.setStatus(
                StringUtils.hasText(incident.getAssignedTo())
                    ? IncidentStatus.IN_PROGRESS
                    : IncidentStatus.REPORTED
        );
        return incident;
    }

    private void addActionsTaken(Incident incident, IncidentRequest request) {
        actionsTakenService.addActionsTaken(incident.getId(), userUtil.getUserName(), request.getActionsTakenByReporter());
    }

    @Transactional
    public IncidentResponse addIncident(IncidentRequest request) {
        Incident incident = createIncidentFromRequest(request);
        save(incident);
        affectedEquipmentService.addAffectedEquipments(incident.getId(), request.getAffectedEquipments());
        addActionsTaken(incident, request);

        return buildIncidentResponse(incident);
    }

    private void updateIncident(Incident incident, IncidentRequest request) {
        incident.setStation(request.getStation());
        incident.setLocation(request.getLocation());

        if (request.getOccurredAt() != null)
            incident.setOccurredAt(request.getOccurredAt());
        // skip reportedAt

        if (StringUtils.hasText(request.getAssignedTo())) {
            incident.setAssignedTo(request.getAssignedTo());
        } else {
            incident.setAssignedTo(null);
        }

        // skip resolvedAt
        if (request.getFaultNature() != null)
            incident.setFaultNature(request.getFaultNature());

        incident.setSummary(request.getSummary());
        incident.setDescription(request.getDescription());

        if (request.getPriority() != null)
            incident.setPriority(request.getPriority());

        if (request.getCategory() != null)
            incident.setCategory(request.getCategory());

        if (request.getDivision() != null)
            incident.setDivision(request.getDivision());
    }

    private void updateActionsTaken(Incident incident, IncidentRequest request) {
        final String userName = userUtil.getUserName();
        if (incidentUtil.isReporterOrAdmin(incident, userName))
            actionsTakenService.updateActionsTaken(incident.getId(), userName, request.getActionsTakenByReporter());

        if (incidentUtil.isAssigneeOrAdmin(incident, userName))
            actionsTakenService.updateActionsTaken(incident.getId(), userName, request.getActionsTakenByAssignee());
    }

    private void updateIncidentStatus(Incident incident, IncidentStatus newStatus) {
        incidentStatusTrackerService.addIncidentStatus(incident, newStatus);
        incident.setStatus(newStatus);
    }

    private void updateStatusDueToAssigneeChange(Incident incident, IncidentRequest request) {
        if (!StringUtils.hasText(incident.getAssignedTo()) && !StringUtils.hasText(request.getAssignedTo())) {
            // no change in assignee
            return;
        }

        if (!StringUtils.hasText(request.getAssignedTo())) {
            if (!IncidentStatus.RETURNED.equals(incident.getStatus())) {
                // this should not occur
                updateIncidentStatus(incident, IncidentStatus.REPORTED);
            }
        } else {
            if (!request.getAssignedTo().equals(incident.getAssignedTo())) {
                // this is a usual case
                updateIncidentStatus(incident, IncidentStatus.IN_PROGRESS);
            }
        }
    }

    @Transactional
    public IncidentResponse updateIncident(final int id, IncidentRequest request) {
        Incident incident = findById(id);
        incidentUtil.checkEditPermission(incident);

        updateStatusDueToAssigneeChange(incident, request);
        updateIncident(incident, request);
        affectedEquipmentService.updateAffectedEquipments(incident.getId(), request.getAffectedEquipments());
        updateActionsTaken(incident, request);
        return buildIncidentResponse(incident);
    }

    private void checkAssigneePermission(Incident incident) {
        if (!incidentUtil.isAssignee(incident) || !IncidentStatus.IN_PROGRESS.equals(incident.getStatus())) {
            throw new AccessDeniedException("You do not have permission to update this incident");
        }
    }

    @Transactional
    public void updateIncidentByAssignee(final int id, UpdateRequestByAssignee request) {
        Incident incident = findById(id);
        checkAssigneePermission(incident);

        if (request.isCompleted()) {
            incident.setRemarksByAssignee(request.getRemarksByAssignee());
            updateIncidentStatus(incident, IncidentStatus.COMPLETED);
        } else {
            incident.setInitialAssignee(incident.getAssignedTo());
            incident.setRemarksByInitialAssignee(request.getRemarksByAssignee());

            incident.setAssignedTo(null);
            incident.setRemarksByAssignee(null);

            updateIncidentStatus(incident, IncidentStatus.RETURNED);
        }
    }

    private void checkSupervisorPermission(Incident incident) {
        if (!userUtil.isSupervisor()) {
            throw new AccessDeniedException("You do not have permission to update this incident");
        }

        if (!IncidentStatus.COMPLETED.equals(incident.getStatus())) {
            throw new AccessDeniedException("You do not have permission to update this incident");
        }
    }

    @Transactional
    public void updateIncidentBySupervisor(final int id, UpdateRequestBySupervisor request) {
        Incident incident = findById(id);
        checkSupervisorPermission(incident);

        incident.setRemarksBySupervisor(request.getRemarksBySupervisor());
        updateIncidentStatus(incident, IncidentStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
    }

    private void checkStatusEditPermission(Incident incident, IncidentStatus newStatus) {
        if (IncidentStatus.RESOLVED.equals(newStatus) && incidentUtil.isAssignee(incident)) {
            throw new AccessDeniedException("You do not have permission to resolve this incident");
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteIncident(int id) {
        affectedEquipmentService.deleteByIncidentId(id);
        actionsTakenService.deleteAllByIncidentId(id);
        incidentRepository.deleteById(id);
    }
}
