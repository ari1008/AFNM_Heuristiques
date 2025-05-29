package org.esgi.users.resources.dto.out;

public record UserResponse(String id, String firstname, String lastname, String email, String role,
                           boolean isElectricOrHybrid, String sessionToken) {
}
