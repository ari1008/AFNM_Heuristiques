package org.esgi.users.resources.in;

public record UserRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String role,
        boolean hasElectricVehicle
) {
}
