package org.esgi.reservation.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.reservation.ReservationService;
import org.esgi.reservation.resources.dto.in.ReservationRequest;
import org.esgi.reservation.resources.dto.in.ReservationUpdateRequest;
import org.esgi.reservation.resources.dto.out.ReservationResponse;
import org.esgi.users.Role;
import org.esgi.users.UserEntity;

import java.util.List;
import java.util.UUID;

@Path("/admin/reservations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminReservationResource {

    @Inject
    ReservationService reservationService;

    @GET
    public List<ReservationResponse> getAll(@Context ContainerRequestContext context) {
        checkSecretary(context);
        return reservationService.getAllReservations();
    }

    @POST
    public Response create(@Context ContainerRequestContext context, ReservationRequest request) {
        checkSecretary(context);
        reservationService.createReservation(request);
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, ReservationUpdateRequest update, @Context ContainerRequestContext context) {
        checkSecretary(context);
        reservationService.updateReservation(id, update);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id, @Context ContainerRequestContext context) {
        checkSecretary(context);
        reservationService.deleteReservation(id);
        return Response.noContent().build();
    }

    private void checkSecretary(ContainerRequestContext context) {
        UserEntity current = (UserEntity) context.getProperty("currentUser");
        if (current == null || current.role != Role.SECRETARY) {
            throw new ForbiddenException("Access reserved to secretaries");
        }
    }
}
