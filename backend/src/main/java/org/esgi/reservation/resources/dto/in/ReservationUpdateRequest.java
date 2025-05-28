package org.esgi.reservation.resources.dto.in;

import java.time.LocalDate;

public class ReservationUpdateRequest {
    public LocalDate startDateTime;
    public LocalDate endDateTime;
}