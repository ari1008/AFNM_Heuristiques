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

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;

    @Column(name = "session_token", nullable = false, unique = true)
    public String sessionToken;

    @Column(name= "is_hybrid_or_electric",nullable = false)
    public boolean isHybridOrElectric;

    public LocalDateTime createdAt = LocalDateTime.now();


    @PrePersist
    public void generateId() {
        if (id == null) id = UUID.randomUUID();
    }

    public static boolean emailExists(String email) {
        return find("email", email.toLowerCase()).firstResultOptional().isPresent();
    }
}

