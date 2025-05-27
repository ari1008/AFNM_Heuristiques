package org.esgi.users;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject UserService service;

    public static record CreateUserRequest(
            String firstname,
            String lastname,
            String email,
            String password,
            String role) { }

    public static record UserResponse(String id, String firstname, String lastname, String email, String role) { }

    @POST
    public Response createUser(CreateUserRequest req) {
        try {
            UserEntity u = service.create(req.firstname(), req.lastname(), req.email(), req.password(), req.role());
            UserResponse body = new UserResponse(u.id.toString(), u.firstname, u.lastname, u.email, u.role.toString());
            return Response.status(Response.Status.CREATED).entity(body).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }
}
