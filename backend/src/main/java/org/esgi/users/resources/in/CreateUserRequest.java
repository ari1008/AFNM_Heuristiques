package org.esgi.users.resources.in;

public record CreateUserRequest(
        String firstname,
        String lastname,
        String email,
        String password,
        String role,
        boolean isElectricOrHybrid) {
}
