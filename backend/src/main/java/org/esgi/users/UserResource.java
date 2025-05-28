package org.esgi.users;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService service;

    public record CreateUserRequest(
            String firstname,
            String lastname,
            String email,
            String password,
            String role,
            boolean isElectricOrHybrid) {
    }

    public record UserResponse(String id, String firstname, String lastname, String email, String role,
                               boolean isElectricOrHybrid) {
    }

    @POST
    public Response createUser(CreateUserRequest req) {
        try {
            UserEntity u = service.create(req);
            UserResponse body = new UserResponse(u.id.toString(), u.firstname, u.lastname, u.email, u.role.toString(), u.isHybridOrElectric);
            return Response.status(Response.Status.CREATED).entity(body).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }
}
