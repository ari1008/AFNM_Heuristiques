package org.esgi.adapters.user;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.application.user.CreateUserHandler;
import org.esgi.shared.dto.UserDto;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    CreateUserHandler handler;

    @POST
    public Response create(UserDto dto) {
        UserDto created = handler.handle(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
