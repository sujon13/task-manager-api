package com.example.incident.model;

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
@Table(name = "affected_equipments", indexes = {
        @Index(name = "idx_affected_equipments_incident_id", columnList = "incident_id")
})
@NoArgsConstructor
public class AffectedEquipment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "incident_id")
    private int incidentId;

    @NotNull
    @Column(name = "equipment", length = 64)
    private String equipment;
}
