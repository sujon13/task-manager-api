package com.example.incident.model;

import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = "actions_taken", indexes = {
        @Index(name = "idx_actions_taken_incident_id_taker", columnList = "incident_id, taker")
})
@NoArgsConstructor
@AllArgsConstructor
public class ActionsTaken extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "incident_id")
    private int incidentId;

    @NotNull
    @Column(name = "taker", length = 64)
    private String taker;

    @NotEmpty
    @Column(name = "action", length = 1024)
    private String action;
}
