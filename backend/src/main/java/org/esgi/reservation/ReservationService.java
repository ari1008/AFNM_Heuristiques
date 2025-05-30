package org.esgi.reservation;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.parking.ParkingSlotEntity;
import org.esgi.parking.ParkingSlotRepository;
import org.esgi.reservation.resources.ReservationMapper;
import org.esgi.reservation.resources.dto.in.ReservationRequest;
import org.esgi.reservation.resources.dto.in.ReservationUpdateRequest;
import org.esgi.reservation.resources.dto.out.ReservationResponse;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Inject
    ReservationMapper reservationMapper;

    @Transactional
    public void createReservation(ReservationRequest request) {
        UserEntity user = userRepository.findById(request.userId);
        ParkingSlotEntity slot = parkingSlotRepository.findById(request.slotId);

        validateReservationDates(request.dates, user);
        validateVehicleCompatibility(user, slot);

        validateNonExistenceOfPreviousReservationTheseDays(request.dates, request.slotId);

        ReservationEntity reservation = reservationMapper.fromRequest(request, user, slot);
        reservationRepository.persist(reservation);
    }

    public List<ReservationResponse> getAllReservationsBySlotId(UUID slotId) {
        List<ReservationEntity> reservations = reservationRepository.findAllBySlotId(slotId);
        return reservationMapper.toResponseList(reservations);
    }

    public void updateReservation(UUID id, ReservationUpdateRequest update) {
        ReservationEntity existing = reservationRepository.findById(id);
        if (existing == null) {
            throw new WebApplicationException("Reservation not found", Response.Status.NOT_FOUND);
        }

        UserEntity user = userRepository.findById(existing.userId);
        ParkingSlotEntity slot = existing.slot;

        validateReservationRange(update.startDateTime, update.endDateTime);
        validateVehicleCompatibility(user, slot);

        reservationMapper.applyUpdate(existing, update);
        reservationRepository.persist(existing);
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationMapper.toResponseList(reservationRepository.listAll());
    }

    @Transactional
    public void deleteReservation(UUID id) {
        ReservationEntity res = reservationRepository.findById(id);
        if (res == null) {
            throw new WebApplicationException("Reservation not found", Response.Status.NOT_FOUND);
        }
        reservationRepository.delete(res);
    }

    @Transactional
    public void checkIn(UUID userId, UUID slotId) {
        ReservationEntity res = reservationRepository.findReservationOfTheDay(userId);
        if (!res.slot.id.equals(slotId)) {
            throw new IllegalArgumentException("Slot does not match today's reservation");
        }
        res.checkedInAt = LocalDateTime.now();
    }

    @Transactional
    @Scheduled(cron = "0 0 11 * * ?", timeZone = "Europe/Paris")
    public void releaseUnattendedReservations() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));
        LocalDateTime cutoff = today.atTime(11, 0);
        LocalDate nextDayStart = today.plusDays(1);

        List<ReservationEntity> unattended = reservationRepository.findUnattendedReservations();
        for (ReservationEntity r : unattended) {
            if (r.endDate.isAfter(nextDayStart)) {
                ReservationEntity tail = new ReservationEntity();
                tail.slot = r.slot;
                tail.userId = r.userId;
                tail.startDate = nextDayStart;
                tail.endDate = r.endDate;
                reservationRepository.persist(tail);
            }
            r.endDate = cutoff.toLocalDate();
        }
    }

    private void validateReservationDates(List<LocalDate> dates, UserEntity user) {
        if (dates == null || dates.isEmpty()) {
            throw new WebApplicationException("Reservation dates cannot be empty", Response.Status.BAD_REQUEST);
        }

        int maxDays = user.role.name().equals("MANAGER") ? 30 : 5;
        if (dates.size() > maxDays) {
            throw new WebApplicationException("Too many reservation days", Response.Status.BAD_REQUEST);
        }

        for (LocalDate date : dates) {
            if (date.isBefore(LocalDate.now())) {
                throw new WebApplicationException("Cannot reserve past dates", Response.Status.BAD_REQUEST);
            }
        }

        LocalDate start = dates.get(0);
        LocalDate end = dates.get(dates.size() - 1);
        validateReservationRange(start, end);
    }

    private void validateNonExistenceOfPreviousReservationTheseDays(List<LocalDate> dates, UUID slotId) {
        LocalDate startDate = dates.stream().min(LocalDate::compareTo).orElseThrow();
        LocalDate endDate = dates.stream().max(LocalDate::compareTo).orElseThrow().plusDays(1);

        boolean overlapExists = reservationRepository.existsBySlotAndDateRange(slotId, startDate, endDate);
        if (overlapExists) {
            throw new IllegalStateException("A reservation already exists for this slot on at least one of the requested dates.");
        }
    }

    private void validateReservationRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new WebApplicationException("Start and end dates are required", Response.Status.BAD_REQUEST);
        }

        if (start.isAfter(end)) {
            throw new WebApplicationException("Start date must be before or equal to end date", Response.Status.BAD_REQUEST);
        }
    }

    private void validateVehicleCompatibility(UserEntity user, ParkingSlotEntity slot) {
        boolean isElectricSlot = slot.row.equalsIgnoreCase("A") || slot.row.equalsIgnoreCase("F");
        if (isElectricSlot && !user.isHybridOrElectric) {
            throw new WebApplicationException("Only electric/hybrid vehicles can use A/F rows", Response.Status.FORBIDDEN);
        }
    }
}
