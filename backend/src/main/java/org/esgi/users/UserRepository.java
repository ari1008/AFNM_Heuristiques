package org.esgi.users;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {
    public UserEntity findById(UUID id) {
        return find("id", id).firstResult();
    }
}
