package org.esgi.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordHasher {
    public String hash(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }
}