package org.esgi.users.resources;

import jakarta.enterprise.context.ApplicationScoped;
import org.esgi.users.Role;
import org.esgi.users.UserEntity;
import org.esgi.users.resources.dto.in.CreateUserRequest;
import org.esgi.users.resources.dto.in.UserUpdateRequest;
import org.esgi.users.resources.dto.out.UserResponse;

import java.util.List;

@ApplicationScoped
public class UserMapper {

    public UserEntity fromCreateRequest(CreateUserRequest dto, String hashedPassword) {
        UserEntity user = new UserEntity();
        user.firstname = dto.firstname();
        user.lastname = dto.lastname();
        user.email = dto.email().toLowerCase();
        user.passwordHash = hashedPassword;
        user.role = Role.valueOf(dto.role().toUpperCase());
        user.isHybridOrElectric = dto.isElectricOrHybrid();
        return user;
    }

    public void applyUpdate(UserEntity user, UserUpdateRequest dto) {
        user.firstname = dto.firstname;
        user.lastname = dto.lastname;
        user.role = Role.valueOf(dto.role);
        user.isHybridOrElectric = dto.isHybridOrElectric;
    }

    public UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.id.toString(),
                user.firstname,
                user.lastname,
                user.email,
                user.role.name(),
                user.isHybridOrElectric
        );
    }
    public List<UserResponse> toResponses(List<UserEntity> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}
