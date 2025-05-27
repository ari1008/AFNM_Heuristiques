package org.esgi.shared.dto;

public class UserDto {
    public String firstname;
    public String lastname;
    public String email;
    public String password;
    public String role;
    public boolean hasElectricVehicle;

    public UserDto withoutPassword() {
        this.password = null;
        return this;
    }
}
