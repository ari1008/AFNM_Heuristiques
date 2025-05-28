package org.esgi.parking;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ParkingInitializer {

    @Inject
    ParkingSlotRepository slotRepository;

    @PostConstruct
    @Transactional
    public void initSlots() {
        if (slotRepository.count() > 0) return;

        for (char row = 'A'; row <= 'F'; row++) {
            for (int num = 1; num <= 10; num++) {
                ParkingSlotEntity slot = new ParkingSlotEntity();
                slot.row = String.valueOf(row);
                slot.number = num;
                slot.code = row + String.format("%02d", num);
                slot.hasCharger = (row == 'A' || row == 'F');
                slotRepository.persist(slot);
            }
        }

        System.out.println("✅ Parking slots initialized (60)");
    }
}
