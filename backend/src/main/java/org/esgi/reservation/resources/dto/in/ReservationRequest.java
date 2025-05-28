package org.esgi.reservation.resources.dto.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReservationRequest {
    public UUID userId;
    public List<LocalDate> dates;
    public UUID slotId;
    public String period;
}
