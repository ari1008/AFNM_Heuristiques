package org.esgi.infrastructure.persistence.user;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.esgi.domain.model.User;

import java.util.UUID;


@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, UUID> {

    @Transactional
    public void save(User user) {
        persist(UserMapper.toEntity(user));
    }

    public User findByEmail(String email) {
        UserEntity entity = find("email", email.toLowerCase()).firstResult();
        return entity != null ? UserMapper.toDomain(entity) : null;
    }
}