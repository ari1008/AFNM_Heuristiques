package org.esgi.authentification.resources.in;

public record LoginRequest(
        String email,
        String password
) {
}
