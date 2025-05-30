package org.esgi.reservation.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.reservation.ReservationService;
import org.esgi.reservation.resources.dto.in.CheckInRequest;
import org.esgi.reservation.resources.dto.in.ReservationRequest;

import java.util.UUID;

@Path("/reservations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    ReservationService reservationService;

    @POST
    public Response create(ReservationRequest request) {
        reservationService.createReservation(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/checkin")
    @jakarta.ws.rs.Consumes(MediaType.APPLICATION_JSON)
    public Response checkIn(CheckInRequest request) {
        if (request == null || request.userId() == null || request.slotId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            reservationService.checkIn(request.userId(), request.slotId());
            return Response.ok().build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }

    @GET
    @Path("/slot/{slotId}")
    public Response getAllBySlot(@PathParam("slotId") UUID slotId) {
        return Response.ok(reservationService.getAllReservationsBySlotId(slotId)).build();
    }

}
