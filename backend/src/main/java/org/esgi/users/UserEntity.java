package org.esgi.users;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false)
    public String firstname;

    @Column(nullable = false)
    public String lastname;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false)
    public Role role;

    @Column
    public String sessionToken;

    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void generateId() {
        if (id == null) id = UUID.randomUUID();
    }

    public static boolean emailExists(String email) {
        return find("email", email.toLowerCase()).firstResultOptional().isPresent();
    }
}

