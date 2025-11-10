package com.example.incident.model;

import com.example.incident.converter.*;
import com.example.incident.enums.*;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Audited
@Table(name = "incidents")
@NoArgsConstructor
public class Incident extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "event_no", unique = true, updatable = false)
    private Integer eventNo;

    @Column(name = "station", length = 64)
    private String station;
    @Column(name = "location", length = 64)
    private String location;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt = LocalDateTime.now();

    @Column(name = "reported_by", length = 64)
    private String reportedBy;

    @Column(name = "assigned_to", length = 64)
    private String assignedTo;

    @Column(name = "pending_to", length = 64)
    private String pendingTo;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "fault_nature")
    @Convert(converter = FaultNatureConverter.class)
    private FaultNature faultNature = FaultNature.SOFTWARE;

    // Affected Equipment -> in separate table

    @Column(name = "summary", length = 1024)
    private String summary;

    @Column(name = "description", length = 2048)
    private String description;

    // actionTakenByScada

    // actionTakenByContractor

    @Column(name = "remarks_by_reporter", length = 1024)
    private String remarksByReporter;

    @Column(name = "remarks_by_assignee", length = 1024)
    private String remarksByAssignee;

    @Column(name = "initial_assignee", length = 64)
    private String initialAssignee;

    @Column(name = "remarks_by_initial_assignee", length = 1024)
    private String remarksByInitialAssignee;

    @Column(name = "remarks_by_supervisor", length = 1024)
    private String remarksBySupervisor;

    @Column(name = "status")
    @Convert(converter = IncidentStatusConverter.class)
    private IncidentStatus status = IncidentStatus.REPORTED;


    @Column(name = "priority")
    @Convert(converter = PriorityConverter.class)
    private Priority priority = Priority.HIGH;

    // category
    @Column(name = "category")
    @Convert(converter = CategoryConverter.class)
    private IncidentCategory category;
    // sub-category

    @Column(name = "division")
    @Convert(converter = DivisionConverter.class)
    private Division division = Division.SCADA;

}


