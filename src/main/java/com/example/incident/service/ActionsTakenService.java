package com.example.incident.service;

import com.example.incident.model.ActionTakenRequest;
import com.example.incident.model.ActionsTaken;
import com.example.incident.repository.ActionsTakenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActionsTakenService {
    private final ActionsTakenRepository actionsTakenRepository;

    public List<ActionsTaken> findAllByIncidentId(int incidentId) {
        return actionsTakenRepository.findAllByIncidentId(incidentId);
    }

    public List<ActionsTaken> findAllByIncidentIdList(List<Integer> incidentIdList) {
        return actionsTakenRepository.findAllByIncidentIdIn(incidentIdList);
    }

    public List<ActionsTaken> findAllByIncidentIdAndTaker(int incidentId, String taker) {
        return actionsTakenRepository.findAllByIncidentIdAndTaker(incidentId, taker);
    }

    public List<String> findAllActionsByIncidentIdAndTaker(int incidentId, String taker) {
        return actionsTakenRepository.findAllByIncidentIdAndTaker(incidentId, taker)
                .stream()
                .map(ActionsTaken::getAction)
                .toList();
    }

    public Map<Integer, List<ActionsTaken>> findIncidentIdToActionsTakenMap(List<Integer> incidentIdList) {
        return actionsTakenRepository.findAllByIncidentIdIn(incidentIdList)
                .stream()
                .collect(Collectors.groupingBy(ActionsTaken::getIncidentId));
    }

    @Transactional
    public void deleteAllByIncidentId(int incidentId) {
        actionsTakenRepository.deleteAllByIncidentId(incidentId);
    }

    public ActionTakenRequest buildActionTakenRequest(ActionsTaken actionsTaken) {
        return ActionTakenRequest.builder()
                .id(actionsTaken.getId())
                .action(actionsTaken.getAction())
                .build();
    }

    private ActionsTaken buildActionsTaken(int incidentId, String username, ActionTakenRequest actionRequest) {
        ActionsTaken actionsTaken = new ActionsTaken();
        actionsTaken.setId(actionRequest.getId());
        actionsTaken.setIncidentId(incidentId);
        actionsTaken.setTaker(username);
        actionsTaken.setAction(actionRequest.getAction());
        return actionsTaken;
    }

    private List<ActionsTaken> buildActionsTaken(int incidentId, String username, List<ActionTakenRequest> actionRequests) {
        return actionRequests.stream()
                .map(actionRequest -> buildActionsTaken(incidentId, username, actionRequest))
                .toList();
    }

    @Transactional
    public void addActionsTaken(int incidentId, String username, List<ActionTakenRequest> actionTakenRequests) {
        List<ActionsTaken> actionsTaken = buildActionsTaken(incidentId, username, actionTakenRequests);
        actionsTakenRepository.saveAll(actionsTaken);
    }

    private void deleteOldActions(int incidentId, String username, List<ActionTakenRequest> actionTakenRequests) {
        Set<Integer> newActionIds = actionTakenRequests.stream()
                .map(ActionTakenRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        List<Integer> deletableActionIds =
                actionsTakenRepository.findAllByIncidentIdAndTaker(incidentId, username)
                        .stream()
                        .map(ActionsTaken::getId)
                        .filter(existingActionId -> !newActionIds.contains(existingActionId))
                        .toList();
        actionsTakenRepository.deleteAllById(deletableActionIds);
    }

    @Transactional
    public void updateActionsTaken(int incidentId, String username, List<ActionTakenRequest> actionTakenRequests) {
        deleteOldActions(incidentId, username, actionTakenRequests);

        List<ActionsTaken> updatedActionsTaken = buildActionsTaken(incidentId, username, actionTakenRequests);
        actionsTakenRepository.saveAll(updatedActionsTaken);
    }

}
