package org.esgi.users.resources.dto.in;

public record CreateUserRequest(
        String firstname,
        String lastname,
        String email,
        String password,
        String role,
        boolean isElectricOrHybrid) {
}
