package org.esgi.users.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.users.Role;
import org.esgi.users.UserEntity;
import org.esgi.users.UserService;
import org.esgi.users.resources.dto.in.UserUpdateRequest;
import java.util.List;
import java.util.UUID;

@Path("/admin/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminUserResource {

    @Inject
    UserService userService;

    @GET
    public List<UserEntity> getAll(@Context ContainerRequestContext context) {
        checkAdmin(context);
        return userService.getAll();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, UserUpdateRequest request, @Context ContainerRequestContext context) {
        checkAdmin(context);
        userService.update(id, request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id, @Context ContainerRequestContext context) {
        checkAdmin(context);
        userService.delete(id);
        return Response.noContent().build();
    }

    private void checkAdmin(ContainerRequestContext context) {
        UserEntity current = (UserEntity) context.getProperty("currentUser");
        if (current == null || current.role != Role.SECRETARY) {
            throw new ForbiddenException("Access reserved to administrators");
        }
    }
}

