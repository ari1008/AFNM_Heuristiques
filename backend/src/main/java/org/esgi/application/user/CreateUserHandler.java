package org.esgi.application.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.esgi.domain.model.Role;
import org.esgi.domain.model.User;
import org.esgi.infrastructure.persistence.user.UserRepository;
import org.esgi.infrastructure.security.PasswordHasher;
import org.esgi.shared.dto.UserDto;

@ApplicationScoped
public class CreateUserHandler {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordHasher passwordHasher;

    public UserDto handle(UserDto dto) {
        String hashedPassword = passwordHasher.hash(dto.password);
        User user = User.createNew(dto.firstname, dto.lastname, dto.email, hashedPassword, Role.valueOf(dto.role), dto.hasElectricVehicle);
        userRepository.save(user);
        return dto.withoutPassword();
    }
}
