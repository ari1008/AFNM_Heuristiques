package org.esgi.parking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ParkingInitializer {

    private static final Logger LOG = Logger.getLogger(ParkingInitializer.class);

    @Inject
    ParkingSlotRepository slotRepository;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (slotRepository.count() > 0) {
            LOG.info("Parking slots already initialized");
            return;
        }

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

        LOG.info("✅ Parking slots initialized (60)");
    }
}
