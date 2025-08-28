package com.example.incident.controller;

import com.example.incident.enums.IncidentStatus;
import com.example.incident.model.IncidentRequest;
import com.example.incident.model.IncidentResponse;
import com.example.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentService incidentService;


    @GetMapping("")
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        List<IncidentResponse> incidentList = incidentService.getIncidents();
        return ResponseEntity.ok(incidentList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncident(@PathVariable final int id) {
        IncidentResponse incident = incidentService.getIncident(id);
        return ResponseEntity.ok(incident);
    }

    @PostMapping("")
    public ResponseEntity<IncidentResponse> addIncident(@Valid @RequestBody final IncidentRequest request) {
        IncidentResponse incidentResponse = incidentService.addIncident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponse> updateIncident(@PathVariable int id, @Valid @RequestBody IncidentRequest request) {
        IncidentResponse incidentResponse = incidentService.updateIncident(id, request);
        return ResponseEntity.ok(incidentResponse);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateIncidentStatus(@PathVariable int id, @Valid @RequestBody IncidentStatus status) {
        IncidentResponse incidentResponse = incidentService.updateIncidentStatus(id, status);
        return ResponseEntity.ok(incidentResponse);
    }

}
