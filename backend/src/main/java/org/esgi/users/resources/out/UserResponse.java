package org.esgi.users.resources.out;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean hasElectricVehicle
) {
}
