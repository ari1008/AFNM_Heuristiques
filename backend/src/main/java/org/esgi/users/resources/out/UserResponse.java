package org.esgi.users.resources.out;

public record UserResponse(String id, String firstname, String lastname, String email, String role,
                           boolean isElectricOrHybrid) {
}
