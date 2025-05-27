package org.esgi.auth.dto;

public class LoginResponse {
    public String token;
    public String email;
    public String role;

    public LoginResponse(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }
}