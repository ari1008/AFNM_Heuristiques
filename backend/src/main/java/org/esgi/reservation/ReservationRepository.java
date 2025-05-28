package org.esgi.reservation;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ReservationRepository implements PanacheRepository<ReservationEntity> {
    public ReservationEntity findById(UUID id) {
        return find("id", id).firstResult();
    }
}