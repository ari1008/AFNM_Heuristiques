package org.esgi.parking.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.esgi.parking.ParkingSlotEntity;
import org.esgi.parking.ParkingSlotService;

import java.time.LocalDate;
import java.util.List;

@Path("/slots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParkingSlotResource {

    @Inject
    ParkingSlotService parkingSlotService;

    @GET
    @Path("/available")
    public List<ParkingSlotEntity> getAvailable(@QueryParam("from") String from,
                                                @QueryParam("to") String to) {
        if (from == null || to == null)
            throw new WebApplicationException("Query params 'from' and 'to' are required", Response.Status.BAD_REQUEST);

        LocalDate start = LocalDate.parse(from);
        LocalDate end   = LocalDate.parse(to);

        if (!start.isBefore(end))
            throw new WebApplicationException("'from' must be before 'to'", Response.Status.BAD_REQUEST);

        return parkingSlotService.findAvailable(start, end);
    }


    @GET
    public List<ParkingSlotEntity> findAllSlots(){
        return parkingSlotService.findAllSlots();
    }
}
