package org.esgi.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.auth.dto.LoginRequest;
import org.esgi.users.Role;
import org.esgi.users.UserEntity;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    public record LoginResponse(String sessionToken, String email, Role role) {}

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        UserEntity user = authService.login(request.email, request.password);
        return Response.ok(new LoginResponse(user.sessionToken, user.email, user.role)).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String token) {
        authService.logout(token);
        return Response.noContent().build();
    }
}
