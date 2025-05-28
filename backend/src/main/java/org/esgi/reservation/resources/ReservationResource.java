package org.esgi.reservation.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.reservation.ReservationService;
import org.esgi.reservation.resources.in.ReservationRequest;

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
}
