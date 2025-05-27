package org.esgi.auth.utils;

import io.smallrye.jwt.build.Jwt;
import org.esgi.users.UserEntity;

import java.time.Duration;

public class JwtUtils {

    public static String generateToken(UserEntity user) {
        return Jwt.issuer("afnm")
                .subject(user.id.toString())
                .claim("email", user.email)
                .claim("role", user.role.toString())
                .expiresIn(Duration.ofHours(4))
                .sign();
    }
}
