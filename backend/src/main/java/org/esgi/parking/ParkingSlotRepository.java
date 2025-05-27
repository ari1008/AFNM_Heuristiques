package org.esgi.parking;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ParkingSlotRepository implements PanacheRepository<ParkingSlotEntity> {

    public Optional<ParkingSlotEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public List<ParkingSlotEntity> findAllSlots() {
        return listAll();
    }
    public ParkingSlotEntity findById(UUID id){
        return find("id", id).firstResult();
    }
}

