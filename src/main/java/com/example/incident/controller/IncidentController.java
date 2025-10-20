package com.example.incident.controller;

import com.example.incident.model.*;
import com.example.incident.service.IncidentService;
import com.example.incident.service.IncidentUtil;
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
    private final IncidentUtil incidentUtil;


    @GetMapping("")
    public ResponseEntity<Page<IncidentResponse>> getAllIncidents(
            IncidentFilterRequest filterRequest,
            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        Page<IncidentResponse> incidentResponses = incidentService.getIncidents(filterRequest, pageable);
        return ResponseEntity.ok(incidentResponses);
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

    @PutMapping("/update-by-assignee/{id}")
    public ResponseEntity<Void> updateIncidentByAssignee(@PathVariable int id, @Valid @RequestBody UpdateRequestByAssignee request) {
        incidentService.updateIncidentByAssignee(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-by-supervisor/{id}")
    public ResponseEntity<Void> updateIncidentBySupervisor(@PathVariable int id, @Valid @RequestBody UpdateRequestBySupervisor request) {
        incidentService.updateIncidentBySupervisor(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable final int id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/priority/dropdown")
    public List<CustomDropdown> getPriorityDropdown() {
        return incidentUtil.getPriorityDropdown();
    }

    @GetMapping("/status/dropdown")
    public List<CustomDropdown> getStatusDropdown() {
        return incidentUtil.getStatusDropdown();
    }

    @GetMapping("/category/dropdown")
    public List<CustomDropdown> getCategoryDropdown() {
        return incidentUtil.getCategoryDropdown();
    }

}
