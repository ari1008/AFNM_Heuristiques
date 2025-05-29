package org.esgi.parking;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ParkingSlotRepository implements PanacheRepository<ParkingSlotEntity> {

    public Optional<ParkingSlotEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public List<ParkingSlotEntity> findAllSlots() {
        return listAll();
    }
    public ParkingSlotEntity findById(UUID id){
        return find("id", id).firstResult();
    }
    public List<ParkingSlotEntity> findAvailableSlots(LocalDate from, LocalDate to) {
        return getEntityManager()
                .createQuery("SELECT ps FROM ParkingSlotEntity ps " +
                        "WHERE NOT EXISTS (" +
                        "  SELECT 1 FROM ReservationEntity r " +
                        "  WHERE r.slot = ps " +
                        "    AND r.startDate < :to " +
                        "    AND r.endDate   > :from" +
                        ")", ParkingSlotEntity.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}

