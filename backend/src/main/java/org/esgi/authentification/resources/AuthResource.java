package org.esgi.authentification.resources;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claims;
import org.esgi.authentification.resources.in.LoginRequest;
import org.esgi.authentification.resources.out.LoginResponse;
import org.esgi.users.User;
import org.esgi.utils.PasswordUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        User user = User.find("email", request.email().toLowerCase()).firstResult();
        if (user == null || !PasswordUtils.verifyPassword(request.password(), user.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = Jwt.issuer("esgi-parking")
                .subject(user.getId().toString())
                .groups((Set<String>) List.of(user.getRole().name()))
                .claim(Claims.full_name.name(), user.getFirstname() + " " + user.getLastname())
                .claim(Claims.email.name(), user.getEmail())
                .expiresIn(Duration.ofHours(8))
                .sign();

        return Response.ok(new LoginResponse(token)).build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return Response.ok("User logged out").build();
    }
}
