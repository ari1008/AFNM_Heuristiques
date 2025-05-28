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
    public String code;

    @Column(nullable = false)
    public String row;

    @Column(nullable = false)
    public int number;

    @Column(nullable = false)
    public boolean hasCharger;

    @PrePersist
    public void assignId() {
        if (id == null) id = UUID.randomUUID();
    }
}
