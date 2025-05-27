package org.esgi.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.auth.dto.LoginRequest;
import org.esgi.auth.dto.LoginResponse;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;
import org.esgi.utils.PasswordHasher;
import org.esgi.auth.utils.JwtUtils;

import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    public LoginResponse authenticate(LoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.find("email", request.email.toLowerCase()).firstResultOptional();
        if (userOpt.isEmpty() || !PasswordHasher.verify(request.password, userOpt.get().passwordHash)) {
            throw new WebApplicationException("Invalid email or password", Response.Status.UNAUTHORIZED);
        }

        var user = userOpt.get();
        var token = JwtUtils.generateToken(user);
        return new LoginResponse(token, user.email, user.role.toString());
    }
}
