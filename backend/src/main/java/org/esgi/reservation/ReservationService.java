package org.esgi.reservation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.esgi.parking.ParkingSlotEntity;
import org.esgi.utils.FileLogger;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class ReservationService {

    @Inject ReservationRepository repo;
    @Inject FileLogger fileLogger;
    @Inject Logger log;

    @Transactional
    public ReservationEntity reserve(UUID userId,
                                     ParkingSlotEntity slot,
                                     LocalDateTime start,
                                     LocalDateTime end) {

        boolean occupied = repo.find("slot = ?1 and ((startDateTime <= ?2 and endDateTime > ?2) or (startDateTime < ?3 and endDateTime >= ?3))",
                slot, start, end).count() > 0;
        if (occupied) {
            throw new IllegalArgumentException("Slot already booked for that time range");
        }

        ReservationEntity r = new ReservationEntity();
        r.userId        = userId;
        r.slot          = slot;
        r.startDateTime = start;
        r.endDateTime   = end;

        repo.persist(r);

        fileLogger.log("RESERVATION: user=" + userId + ", slot=" + slot.code + ", " + start + "-" + end);
        log.infov("Reservation created (id={0})", r.id);
        return r;
    }
}
