package com.example.incident.service;

import com.example.incident.enums.Division;
import com.example.incident.enums.IncidentCategory;
import com.example.incident.enums.IncidentStatus;
import com.example.incident.enums.Priority;
import com.example.incident.model.CustomDropdown;
import com.example.incident.model.Incident;
import com.example.incident.model.IncidentResponse;
import com.example.util.Constants;
import com.example.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncidentUtil {
    private final UserUtil userUtil;


    public boolean isAssignee(Incident incident, String userName) {
        return Objects.equals(incident.getAssignedTo(), userName);
    }

    public boolean isAssignee(Incident incident) {
        return isAssignee(incident, userUtil.getUserName());
    }

    public boolean isReporter(Incident incident, String userName) {
        return incident.getReportedBy().equals(userName);
    }

    public boolean isReporter(Incident incident) {
        return isReporter(incident, userUtil.getUserName());
    }

    public boolean isReporterOrAdmin(Incident incident, String userName) {
        return isReporter(incident, userName) || userUtil.isAdmin();
    }

    public boolean isReporterOrAdmin(Incident incident) {
        return isReporterOrAdmin(incident, userUtil.getUserName());
    }

    public boolean isAssigneeOrAdmin(Incident incident, String userName) {
        return isAssignee(incident, userName) || userUtil.isAdmin();
    }

    public boolean isAssigneeOrAdmin(Incident incident) {
        return isAssigneeOrAdmin(incident, userUtil.getUserName());
    }

    public boolean isPendingToSuperVisor(IncidentResponse incidentResponse) {
        final String pendingTo = incidentResponse.getPendingTo().getUserName();
        return List.of(Constants.SMD_XEN_USER_NAME, Constants.CNST_XEN_USER_NAME).contains(pendingTo);
    }

    public void checkEditPermission(Incident incident) {
        final String userName = userUtil.getUserName();
        if (userUtil.isSupervisor() || isAssignee(incident, userName) ||
                (isReporter(incident, userName) && IncidentStatus.REPORTED.equals(incident.getStatus()))
        ) {} else {
            log.error("User {} does not have permission to edit incident {}", userName, incident.getId());
            throw new AccessDeniedException("You do not have permission to edit this incident");
        }
    }

    private CustomDropdown buildDropdown(final Priority priority) {
        return new CustomDropdown(priority.getName(), priority.getDisplayName());
    }

    private CustomDropdown buildDropdown(final IncidentStatus incidentStatus) {
        return new CustomDropdown(incidentStatus.getName(), incidentStatus.getDisplayName());
    }

    private CustomDropdown buildDropdown(final IncidentCategory incidentCategory) {
        return new CustomDropdown(incidentCategory.getName(), incidentCategory.getDisplayName());
    }

    private CustomDropdown buildDropdown(final Division division) {
        return new CustomDropdown(division.getName(), division.getDisplayName());
    }

    public List<CustomDropdown> getPriorityDropdown() {
        return Arrays.stream(Priority.values())
                .map(this::buildDropdown)
                .toList();
    }

    public List<CustomDropdown> getStatusDropdown() {
        return Arrays.stream(IncidentStatus.values())
                .map(this::buildDropdown)
                .toList();
    }

    public List<CustomDropdown> getCategoryDropdown() {
        return Arrays.stream(IncidentCategory.values())
                .sorted(Comparator.comparing(IncidentCategory::getName))
                .map(this::buildDropdown)
                .toList();
    }

    public List<CustomDropdown> getDivisionDropdown() {
        return Arrays.stream(Division.values())
                .map(this::buildDropdown)
                .toList();
    }

}
