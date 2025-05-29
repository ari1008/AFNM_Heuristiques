package org.esgi.auth.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.auth.AuthService;
import org.esgi.auth.resource.dto.LoginRequest;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;


    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        return Response.ok(authService.login(request.email, request.password)).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String token) {
        authService.logout(token);
        return Response.noContent().build();
    }
}
