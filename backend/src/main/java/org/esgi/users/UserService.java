package org.esgi.users;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.esgi.utils.PasswordHasher;
import org.jboss.logging.Logger;

import java.util.UUID;


@ApplicationScoped
public class UserService {

    @Inject UserRepository repo;
    @Inject
    PasswordHasher hasher;
    @Inject
    Logger log;

    @Transactional
    public UserEntity create(String firstname,
                             String lastname,
                             String email,
                             String plainPassword,
                             String role) {

        if (UserEntity.emailExists(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (plainPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        UserEntity user = new UserEntity();
        user.firstname    = firstname;
        user.lastname     = lastname;
        user.email        = email.toLowerCase();
        user.passwordHash = hasher.hash(plainPassword);
        user.role         = Role.valueOf(role.toUpperCase());

        repo.persist(user);
        log.infov("User {0} created (id={1})", user.email, user.id);
        return user;
    }

    public UserEntity find(UUID id) {
        return repo.findById(id);
    }
}
