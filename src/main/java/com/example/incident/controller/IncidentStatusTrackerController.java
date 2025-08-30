package com.example.incident.controller;

import com.example.incident.model.StatusTrackerResponse;
import com.example.incident.service.IncidentStatusTrackerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/incidents")
public class IncidentStatusTrackerController {
    private final IncidentStatusTrackerService incidentStatusTrackerService;


    @GetMapping("/{id}/status")
    public ResponseEntity<List<StatusTrackerResponse>> getIncidentStatusHistory(@PathVariable int id) {
        List<StatusTrackerResponse> statusTrackerResponseList = incidentStatusTrackerService.findAllByIncidentId(id);
        return ResponseEntity.ok(statusTrackerResponseList);
    }

}
