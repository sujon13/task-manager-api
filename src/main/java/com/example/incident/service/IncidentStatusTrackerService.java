package com.example.incident.service;

import com.example.incident.enums.IncidentStatus;
import com.example.incident.model.Incident;
import com.example.incident.model.IncidentStatusTracker;
import com.example.incident.model.StatusTrackerResponse;
import com.example.incident.repository.IncidentStatusTrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncidentStatusTrackerService {
    private final IncidentStatusTrackerRepository incidentStatusRepository;


    private IncidentStatusTracker buildIncidentStatus(Incident incident,
                                                      IncidentStatus oldStatus, IncidentStatus newStatus) {
        IncidentStatusTracker incidentStatusTracker = new IncidentStatusTracker();
        incidentStatusTracker.setIncidentId(incident.getId());
        incidentStatusTracker.setOldStatus(oldStatus);
        incidentStatusTracker.setNewStatus(newStatus);
        return incidentStatusTracker;
    }

    private IncidentStatusTracker buildIncidentStatus(Incident incident, IncidentStatus newStatus) {
        return buildIncidentStatus(incident, incident.getStatus(), newStatus);
    }

    @Transactional
    public void addIncidentStatus(Incident incident, IncidentStatus oldStatus, IncidentStatus newStatus) {
        IncidentStatusTracker incidentStatusTracker = buildIncidentStatus(incident, oldStatus, newStatus);
        incidentStatusRepository.save(incidentStatusTracker);
    }

    @Transactional
    public void addIncidentStatus(Incident incident, IncidentStatus newStatus) {
        addIncidentStatus(incident, incident.getStatus(), newStatus);
    }

    private StatusTrackerResponse buildStatusHistory(IncidentStatusTracker incidentStatusTracker) {
        return StatusTrackerResponse.builder()
                .changedBy(incidentStatusTracker.getCreatedBy())
                .changedAt(incidentStatusTracker.getCreatedAt())
                .status(incidentStatusTracker.getNewStatus())
                .build();
    }

    private List<StatusTrackerResponse> buildStatusHistoryList(List<IncidentStatusTracker> incidentStatusTrackerList) {
        return incidentStatusTrackerList.stream()
                .map(this::buildStatusHistory)
                .toList();
    }

    public List<StatusTrackerResponse> findAllByIncidentId(int incidentId) {
        List<IncidentStatusTracker> incidentStatusTrackerList = incidentStatusRepository.findAllByIncidentId(incidentId);
        return buildStatusHistoryList(incidentStatusTrackerList);
    }
}
