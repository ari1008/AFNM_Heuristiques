package org.esgi.reservation;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ReservationRepository implements PanacheRepository<ReservationEntity> {

    public ReservationEntity findById(UUID id) {
        return find("id", id).firstResult();
    }

    public ReservationEntity findReservationOfTheDay(UUID userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));
        LocalDate dayStart = today;
        LocalDate dayEnd   = today.plusDays(1);
        return getEntityManager()
                .createQuery("SELECT r FROM ReservationEntity r WHERE r.userId = :uid " +
                        "AND r.startDate < :dayEnd AND r.endDate > :dayStart", ReservationEntity.class)
                .setParameter("uid", userId)
                .setParameter("dayStart", dayStart)
                .setParameter("dayEnd", dayEnd)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No reservation today for user"));
    }

    public List<ReservationEntity> findUnattendedReservations() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));
        return getEntityManager()
                .createQuery("SELECT r FROM ReservationEntity r WHERE r.checkedInAt IS NULL " +
                        "AND r.startDate < :today AND r.endDate > :today", ReservationEntity.class)
                .setParameter("today", today)
                .getResultList();
    }

    public List<ReservationEntity> findAllBySlotId(UUID slotId) {
        return list("slot.id", slotId);
    }

}