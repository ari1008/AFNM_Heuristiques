package org.esgi.reservation;

import org.esgi.parking.ParkingSlotEntity;
import org.esgi.parking.ParkingSlotRepository;
import org.esgi.reservation.resources.ReservationMapper;
import org.esgi.reservation.resources.dto.in.ReservationRequest;
import org.esgi.users.Role;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

    @InjectMocks
    ReservationService service;

    @Mock ReservationRepository reservationRepo;
    @Mock
    UserRepository userRepo;
    @Mock
    ParkingSlotRepository slotRepo;
    @Mock
    ReservationMapper mapper;

    @Test
    void createReservation_success() {
        UUID userId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.id = userId;
        user.role = Role.EMPLOYEE;
        user.isHybridOrElectric = false;

        ParkingSlotEntity slot = new ParkingSlotEntity();
        slot.id = slotId;
        slot.row = "B";

        ReservationRequest req = new ReservationRequest();
        req.userId = userId;
        req.slotId = slotId;
        req.dates = List.of(LocalDate.now().plusDays(1));

        when(userRepo.findById(userId)).thenReturn(user);
        when(slotRepo.findById(slotId)).thenReturn(slot);
        when(reservationRepo.existsBySlotAndDateRange(any(), any(), any())).thenReturn(false);
        when(mapper.fromRequest(eq(req), eq(user), eq(slot))).thenReturn(new ReservationEntity());

        assertDoesNotThrow(() -> service.createReservation(req));
        verify(reservationRepo).persist(any(ReservationEntity.class));
    }

    @Test
    void createReservation_overlap_throws() {
        UUID userId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.id = userId;
        user.role = Role.EMPLOYEE;
        user.isHybridOrElectric = false;

        ParkingSlotEntity slot = new ParkingSlotEntity();
        slot.id = slotId;
        slot.row = "B";

        ReservationRequest req = new ReservationRequest();
        req.userId = userId;
        req.slotId = slotId;
        req.dates = List.of(LocalDate.now().plusDays(1));

        when(userRepo.findById(userId)).thenReturn(user);
        when(slotRepo.findById(slotId)).thenReturn(slot);
        when(reservationRepo.existsBySlotAndDateRange(any(), any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.createReservation(req));
        verify(reservationRepo, never()).persist(any(ReservationEntity.class));
    }
}
