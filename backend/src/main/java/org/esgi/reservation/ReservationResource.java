package org.esgi.reservation;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    ReservationService service;

    public static record ReserveRequest(String userId, String slotId, String startDateTime, String endDateTime) { }
    public static record ReservationResponse(String id, String userId, String slotCode, String startDateTime, String endDateTime) { }

    @POST
    public Response reserve(ReserveRequest req) {
        try {
            org.esgi.parking.ParkingSlotEntity slot = org.esgi.parking.ParkingSlotEntity.findById(UUID.fromString(req.slotId()));
            if (slot == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Slot not found").build();
            }

            LocalDateTime start = LocalDateTime.parse(req.startDateTime());
            LocalDateTime end   = LocalDateTime.parse(req.endDateTime());

            ReservationEntity r = service.reserve(UUID.fromString(req.userId()), slot, start, end);
            ReservationResponse body = new ReservationResponse(r.id.toString(), r.userId.toString(), r.slot.code, r.startDateTime.toString(), r.endDateTime.toString());
            return Response.status(Response.Status.CREATED).entity(body).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
}

