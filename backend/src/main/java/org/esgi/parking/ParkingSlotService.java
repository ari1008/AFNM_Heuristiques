package org.esgi.parking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ParkingSlotService {

    @Inject
    ParkingSlotRepository slotRepository;


    @Transactional
    public List<ParkingSlotEntity> findAvailable(LocalDate from, LocalDate to) {
        return slotRepository.findAvailableSlots(from, to);
    }
}