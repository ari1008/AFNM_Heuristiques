package org.esgi.reservation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.parking.ParkingSlotEntity;
import org.esgi.parking.ParkingSlotRepository;
import org.esgi.reservation.resources.in.ReservationRequest;
import org.esgi.reservation.resources.in.ReservationUpdateRequest;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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

        for (LocalDate date : request.dates) {
            if (date.isBefore(LocalDate.now())) {
                throw new WebApplicationException("Cannot reserve past dates", Response.Status.BAD_REQUEST);
            }

            LocalTime start = request.period.equalsIgnoreCase("AM") ? LocalTime.of(8, 0) : LocalTime.of(13, 0);
            LocalTime end = request.period.equalsIgnoreCase("AM") ? LocalTime.of(12, 0) : LocalTime.of(18, 0);

            LocalDateTime startDateTime = LocalDateTime.of(date, start);
            LocalDateTime endDateTime = LocalDateTime.of(date, end);

            validateReservation(user, slot, startDateTime, endDateTime);

            ReservationEntity reservation = new ReservationEntity();
            reservation.userId = user.id;
            reservation.slot = slot;
            reservation.startDateTime = startDateTime;
            reservation.endDateTime = endDateTime;

            reservationRepository.persist(reservation);
        }
    }

    public void updateReservation(UUID id, ReservationUpdateRequest update) {
        ReservationEntity existing = reservationRepository.findById(id);
        if (existing == null) {
            throw new WebApplicationException("Reservation not found", Response.Status.NOT_FOUND);
        }

        UserEntity user = userRepository.findById(existing.userId);
        ParkingSlotEntity slot = existing.slot;

        validateReservation(user, slot, update.startDateTime, update.endDateTime);

        existing.startDateTime = update.startDateTime;
        existing.endDateTime = update.endDateTime;

        reservationRepository.persist(existing);
    }

    private void validateReservation(UserEntity user, ParkingSlotEntity slot, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new WebApplicationException("Cannot reserve in the past", Response.Status.BAD_REQUEST);
        }

        boolean isElectricSlot = slot.row.equalsIgnoreCase("A") || slot.row.equalsIgnoreCase("F");
        if (isElectricSlot && !user.isHybridOrElectric) {
            throw new WebApplicationException("Only electric/hybrid vehicles can use A/F rows", Response.Status.FORBIDDEN);
        }

        if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
            throw new WebApplicationException("Reservation must be on a single day", Response.Status.BAD_REQUEST);
        }

        LocalTime start = startDateTime.toLocalTime();
        LocalTime end = endDateTime.toLocalTime();

        boolean isValidAM = start.equals(LocalTime.of(8, 0)) && end.equals(LocalTime.of(12, 0));
        boolean isValidPM = start.equals(LocalTime.of(13, 0)) && end.equals(LocalTime.of(18, 0));

        if (!isValidAM && !isValidPM) {
            throw new WebApplicationException("Time must be 08:00–12:00 or 13:00–18:00", Response.Status.BAD_REQUEST);
        }
    }

    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.listAll();
    }
}
