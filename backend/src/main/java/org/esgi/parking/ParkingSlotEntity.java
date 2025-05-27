package org.esgi.parking;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "parking_slots")
public class ParkingSlotEntity extends PanacheEntityBase {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false, unique = true)
    public String code;          // ex: A01, B03

    @Column(nullable = false)
    public boolean electric;     // borne de recharge ?

    @PrePersist
    public void generateId() { if (id == null) id = UUID.randomUUID(); }
}

