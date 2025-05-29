package org.esgi.reservation.resources.dto.in;

import java.util.UUID;

public record CheckInRequest(
        UUID userId,
        UUID slotId
) {
}
