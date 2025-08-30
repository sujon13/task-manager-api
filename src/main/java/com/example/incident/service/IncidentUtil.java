package com.example.incident.service;

import com.example.incident.model.Incident;
import com.example.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncidentUtil {
    private final UserUtil userUtil;

    public boolean isAssignee(Incident incident) {
        String userName = userUtil.getUserName();
        return incident.getAssignedTo().equals(userName);
    }

    public void checkEditPermission(Incident incident) {
        userUtil.checkEditPermission(incident);

        if (!isAssignee(incident)) {
            throw new AccessDeniedException("You do not have permission to edit this incident");
        }
    }
}
