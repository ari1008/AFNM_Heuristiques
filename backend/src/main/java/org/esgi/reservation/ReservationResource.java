package org.esgi.reservation;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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

    @GET
    public List<ReservationEntity> getAll() {
        return reservationService.getAllReservations();
    }
}
