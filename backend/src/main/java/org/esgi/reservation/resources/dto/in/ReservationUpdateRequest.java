package org.esgi.reservation.resources.dto.in;

import java.time.LocalDateTime;

public class ReservationUpdateRequest {
    public LocalDateTime startDateTime;
    public LocalDateTime endDateTime;
}