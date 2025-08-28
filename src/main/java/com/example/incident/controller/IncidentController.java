package com.example.incident.controller;

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


    @PostMapping("")
    public ResponseEntity<Void> addIncident(@Valid @RequestBody final IncidentRequest request) {
        log.info(request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

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
}
