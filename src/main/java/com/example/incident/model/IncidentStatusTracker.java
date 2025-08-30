package com.example.incident.model;

import com.example.incident.enums.IncidentStatus;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Setter
@Getter
@Entity
@Audited
@Table(name = "incident_status_tracker", indexes = {
        @Index(name = "idx_incident_status_tracker_incident_id", columnList = "incident_id")
})
@NoArgsConstructor
@AllArgsConstructor
public class IncidentStatusTracker extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "incident_id")
    private int incidentId;

    private IncidentStatus oldStatus;

    @NotNull
    private IncidentStatus newStatus;
}
