package org.esgi.infrastructure.persistence.user;

import org.esgi.domain.model.Role;
import org.esgi.domain.model.User;

public class UserMapper {

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstname(user.getFirstname());
        entity.setLastname(user.getLastname());
        entity.setEmail(user.getEmail());
        entity.setHashedPassword(user.getHashedPassword());
        entity.setRole(Role.valueOf(user.getRole().name()));
        entity.setHasElectricVehicle(user.isHasElectricVehicle());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getEmail(),
                entity.getHashedPassword(),
                Role.valueOf(entity.getRole().name()),
                entity.isHasElectricVehicle()
        );
    }
}


