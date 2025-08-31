package com.example.incident.controller;

import com.example.incident.model.IncidentRequest;
import com.example.incident.model.IncidentResponse;
import com.example.incident.model.IncidentUpdateRequest;
import com.example.incident.service.IncidentService;
import com.example.util.Dropdown;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<IncidentResponse>> getAllIncidents(
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        Page<IncidentResponse> incidentList = incidentService.getIncidents(pageable);
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
    public ResponseEntity<IncidentResponse> updateIncidentStatus(@PathVariable int id,
                                                                 @Valid @RequestBody IncidentUpdateRequest updateRequest) {
        IncidentResponse incidentResponse = incidentService.updateIncidentStatus(id, updateRequest);
        return ResponseEntity.ok(incidentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable final int id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/priority/dropdown")
    public List<Dropdown> getPriorityDropdown() {
        return incidentService.getPriorityDropdown();
    }

}
