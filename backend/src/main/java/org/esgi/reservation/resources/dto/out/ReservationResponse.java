package org.esgi.reservation.resources.dto.out;

import java.time.LocalDate;

public record ReservationResponse(
        String id,
        String userId,
        String slotId,
        LocalDate startDate,
        LocalDate endDate
) {}
