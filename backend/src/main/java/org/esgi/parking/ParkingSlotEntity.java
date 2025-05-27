package org.esgi.parking;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "parking_slots", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class ParkingSlotEntity extends PanacheEntityBase {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false, unique = true)
    public String code; // e.g., A01, B07, F10

    @Column(nullable = false)
    public String row;  // A to F

    @Column(nullable = false)
    public int number;  // 1 to 10

    @Column(nullable = false)
    public boolean hasCharger;

    @PrePersist
    public void assignId() {
        if (id == null) id = UUID.randomUUID();
    }
}
