package org.esgi.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;
import org.esgi.users.resources.UserMapper;
import org.esgi.users.resources.dto.out.UserResponse;
import org.esgi.utils.PasswordHasher;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;
    @Inject
    UserMapper userMapper;

    @Transactional
    public UserResponse login(String email, String password) {
        Optional<UserEntity> userOpt = userRepository.find("email", email.toLowerCase()).firstResultOptional();
        if (userOpt.isEmpty() || !PasswordHasher.verify(password, userOpt.get().passwordHash)) {
            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
        }

        UserEntity user = userOpt.get();
        user.sessionToken = UUID.randomUUID().toString();
        userRepository.persist(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void logout(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Token ")) {
            throw new WebApplicationException("Invalid token", Response.Status.BAD_REQUEST);
        }
        String token = tokenHeader.substring("Token ".length());
        Optional<UserEntity> userOpt = userRepository.find("sessionToken", token).firstResultOptional();
        userOpt.ifPresent(user -> {
            user.sessionToken = null;
            userRepository.persist(user);
        });
    }
}
