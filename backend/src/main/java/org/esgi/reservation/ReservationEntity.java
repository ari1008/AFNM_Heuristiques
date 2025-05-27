package org.esgi.reservation;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.esgi.parking.ParkingSlotEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"slot_id", "startDateTime"}))
public class ReservationEntity extends PanacheEntityBase {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false)
    public UUID userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "slot_id")
    public ParkingSlotEntity slot;


    @Column(nullable = false)
    public LocalDateTime startDateTime;

    @Column(nullable = false)
    public LocalDateTime endDateTime;

    @PrePersist
    public void generateId() { if (id == null) id = UUID.randomUUID(); }
}
