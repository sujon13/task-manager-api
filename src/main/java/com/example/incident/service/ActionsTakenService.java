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
    public void deleteByIncidentId(int incidentId) {
        actionsTakenRepository.deleteAllByIncidentId(incidentId);
    }

    public ActionTakenRequest buildActionTakenRequest(ActionsTaken actionsTaken) {
        return ActionTakenRequest.builder()
                .id(actionsTaken.getId())
                .action(actionsTaken.getAction())
                .build();
    }

    private ActionsTaken buildActionsTaken(int incidentId, String username, String action) {
        ActionsTaken actionsTaken = new ActionsTaken();
        actionsTaken.setIncidentId(incidentId);
        actionsTaken.setTaker(username);
        actionsTaken.setAction(action);
        return actionsTaken;
    }

    private List<ActionsTaken> buildActionsTaken(int incidentId, String username, List<ActionTakenRequest> actionRequests) {
        return actionRequests.stream()
                .map(actionRequest -> buildActionsTaken(incidentId, username, actionRequest.getAction()))
                .toList();
    }

    @Transactional
    public void addActionsTaken(int incidentId, String username, List<ActionTakenRequest> actionTakenRequests) {
        List<ActionsTaken> actionsTaken = buildActionsTaken(incidentId, username, actionTakenRequests);
        actionsTakenRepository.saveAll(actionsTaken);
    }

    /*@Transactional
    public void updateAffectedEquipments(int incidentId, List<String> equipments) {
        deleteByIncidentId(incidentId);
        addAffectedEquipments(incidentId, equipments);
    }*/

}
