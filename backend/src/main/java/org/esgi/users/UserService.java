package org.esgi.users;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.esgi.users.resources.UserMapper;
import org.esgi.users.resources.dto.in.CreateUserRequest;
import org.esgi.users.resources.dto.in.UserUpdateRequest;
import org.esgi.users.resources.dto.out.UserResponse;
import org.esgi.utils.PasswordHasher;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository repo;
    @Inject
    PasswordHasher hasher;
    @Inject
    Logger log;
    @Inject
    UserMapper mapper;
    @Inject
    UserMapper userMapper;

    @Transactional
    public UserResponse create(CreateUserRequest createUserRequest) {
        if (UserEntity.emailExists(createUserRequest.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (createUserRequest.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        String hashed = hasher.hash(createUserRequest.password());
        UserEntity user = mapper.fromCreateRequest(createUserRequest, hashed);
        repo.persist(user);
        log.infov("User {0} created (id={1})", user.email, user.id);
        return mapper.toResponse(user);
    }


    public UserResponse find(UUID id) {
        return userMapper.toResponse(repo.findById(id));
    }

    public List<UserResponse> getAll() {
        return userMapper.toResponses(repo.listAll());
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

        mapper.applyUpdate(user, request);
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
