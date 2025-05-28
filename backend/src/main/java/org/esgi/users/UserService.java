package org.esgi.users;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.users.resources.UserResource;
import org.esgi.users.resources.in.CreateUserRequest;
import org.esgi.users.resources.in.UserUpdateRequest;
import org.esgi.utils.PasswordHasher;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject UserRepository repo;
    @Inject
    PasswordHasher hasher;
    @Inject
    Logger log;

    @Transactional
    public UserEntity create(CreateUserRequest createUserRequest) {
        if (UserEntity.emailExists(createUserRequest.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (createUserRequest.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        UserEntity user = new UserEntity();
        user.firstname = createUserRequest.firstname();
        user.lastname = createUserRequest.lastname();
        user.email = createUserRequest.email().toLowerCase();
        user.passwordHash = hasher.hash(createUserRequest.password());
        user.role = Role.valueOf(createUserRequest.role().toUpperCase());
        user.isHybridOrElectric = createUserRequest.isElectricOrHybrid();

        repo.persist(user);
        log.infov("User {0} created (id={1})", user.email, user.id);
        return user;
    }

    public UserEntity find(UUID id) {
        return repo.findById(id);
    }

    public List<UserEntity> getAll() {
        return repo.listAll();
    }

    @Transactional
    public void update(UUID id, UserUpdateRequest request) {
        UserEntity user = repo.findById(id);
        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }

        if (!isValidRole(request.role)) {
            throw new WebApplicationException("Invalid role", Response.Status.BAD_REQUEST);
        }

        user.firstname = request.firstname;
        user.lastname = request.lastname;
        user.role = Role.valueOf(request.role);
        user.isHybridOrElectric = request.isHybridOrElectric;
        repo.persist(user);
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity user = repo.findById(id);
        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }

        repo.delete(user);
    }

    private boolean isValidRole(String role) {
        try {
            Role.valueOf(role);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
