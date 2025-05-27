package org.esgi.reservation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.parking.ParkingSlotEntity;
import org.esgi.parking.ParkingSlotRepository;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReservationService {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ParkingSlotRepository parkingSlotRepository;

    public void createReservation(ReservationRequest request) {
        UserEntity user = userRepository.findById(request.userId);

        ParkingSlotEntity slot = parkingSlotRepository.findById(request.slotId);

        int maxDays = user.role.name().equals("MANAGER") ? 30 : 5;
        if (request.dates.size() > maxDays) {
            throw new WebApplicationException("Too many reservation days", Response.Status.BAD_REQUEST);
        }

        boolean isElectricSlot = slot.row.equalsIgnoreCase("A") || slot.row.equalsIgnoreCase("F");
        if (isElectricSlot && !user.isHybridOrElectric) {
            throw new WebApplicationException("Only electric/hybrid vehicles can use A/F rows", Response.Status.FORBIDDEN);
        }

        for (LocalDate date : request.dates) {
            if (date.isBefore(LocalDate.now())) {
                throw new WebApplicationException("Cannot reserve past dates", Response.Status.BAD_REQUEST);
            }

            LocalTime start = request.period.equalsIgnoreCase("AM") ? LocalTime.of(8, 0) : LocalTime.of(13, 0);
            LocalTime end = request.period.equalsIgnoreCase("AM") ? LocalTime.of(12, 0) : LocalTime.of(18, 0);

            ReservationEntity reservation = new ReservationEntity();
            reservation.userId = user.id;
            reservation.slot = slot;
            reservation.startDateTime = LocalDateTime.of(date, start);
            reservation.endDateTime = LocalDateTime.of(date, end);

            reservationRepository.persist(reservation);
        }
    }

    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.listAll();
    }
}
