package org.esgi.reservation.resources;

import jakarta.enterprise.context.ApplicationScoped;
import org.esgi.parking.ParkingSlotEntity;
import org.esgi.reservation.ReservationEntity;
import org.esgi.reservation.resources.dto.in.ReservationRequest;
import org.esgi.reservation.resources.dto.in.ReservationUpdateRequest;
import org.esgi.reservation.resources.dto.out.ReservationResponse;
import org.esgi.users.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ReservationMapper {

    public ReservationEntity fromRequest(ReservationRequest dto, UserEntity user, ParkingSlotEntity slot) {
        if (dto.dates == null || dto.dates.isEmpty()) {
            throw new IllegalArgumentException("Reservation dates cannot be empty");
        }

        LocalDate start = dto.dates.get(0);
        LocalDate end = dto.dates.get(dto.dates.size() - 1);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.userId = user.id;
        reservation.slot = slot;
        reservation.startDate = start;
        reservation.endDate = end;
        return reservation;
    }


    public void applyUpdate(ReservationEntity entity, ReservationUpdateRequest update) {
        if (update.startDateTime == null || update.endDateTime == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }

        if (update.startDateTime.isAfter(update.endDateTime)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        entity.startDate = update.startDateTime;
        entity.endDate = update.endDateTime;
    }

    public ReservationResponse toResponse(ReservationEntity entity) {
        boolean isCheckedIn = entity.checkedInAt != null;
        return new ReservationResponse(
                entity.id.toString(),
                entity.userId.toString(),
                entity.slot.id.toString(),
                entity.startDate,
                entity.endDate,
                isCheckedIn
        );
    }

    public List<ReservationResponse> toResponseList(List<ReservationEntity> entities) {
        return entities.stream().map(this::toResponse).toList();
    }
}
