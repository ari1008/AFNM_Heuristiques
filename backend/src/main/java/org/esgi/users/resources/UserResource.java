package org.esgi.users.resources;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.users.Role;
import org.esgi.users.User;
import org.esgi.users.resources.in.UserRequest;
import org.esgi.users.resources.out.UserResponse;
import org.esgi.utils.PasswordUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<UserResponse> getAll() {
        return User.listAll().stream()
                .map(user -> toResponse((User) user))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public UserResponse getById(@PathParam("id") UUID id) {
        User user = User.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return toResponse(user);
    }

    @POST
    @Transactional
    public Response create(UserRequest request) {
        User user = new User();
        user.setFirstname(request.firstName());
        user.setLastname(request.lastName());
        user.setEmail(request.email());
        user.setPassword(PasswordUtils.hashPassword(request.password()));
        user.setRole(Role.valueOf(request.role().toUpperCase()));
        user.setHasElectricVehicle(request.hasElectricVehicle());
        user.persist();

        return Response.status(Response.Status.CREATED).entity(toResponse(user)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") UUID id, UserRequest request) {
        User user = User.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        user.setFirstname(request.firstName());
        user.setLastname(request.lastName());
        user.setEmail(request.email());
        user.setPassword(PasswordUtils.hashPassword(request.password()));
        user.setRole(Role.valueOf(request.role().toUpperCase()));
        user.setHasElectricVehicle(request.hasElectricVehicle());
        return Response.ok(toResponse(user)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = User.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("User not found");
        }
        return Response.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),
                user.getFirstname(), user.getLastname(), user.getEmail(),
                user.getRole().name(), user.isHasElectricVehicle());
    }
}

