package org.esgi.domain.model;


import java.util.UUID;

public class User {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String hashedPassword;
    private Role role;
    private boolean hasElectricVehicle;

    public User(UUID id, String firstname, String lastname, String email, String hashedPassword, Role role, boolean hasElectricVehicle) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email.toLowerCase();
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.hasElectricVehicle = hasElectricVehicle;
    }

    public static User createNew(String firstname, String lastname, String email, String hashedPassword, Role role, boolean hasElectricVehicle) {
        return new User(UUID.randomUUID(), firstname, lastname, email, hashedPassword, role, hasElectricVehicle);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isHasElectricVehicle() {
        return hasElectricVehicle;
    }

    public void setHasElectricVehicle(boolean hasElectricVehicle) {
        this.hasElectricVehicle = hasElectricVehicle;
    }
}
