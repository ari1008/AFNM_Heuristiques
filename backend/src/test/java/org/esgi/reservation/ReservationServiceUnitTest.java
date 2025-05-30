package org.esgi.reservation;

import org.esgi.parking.ParkingSlotEntity;
import org.esgi.parking.ParkingSlotRepository;
import org.esgi.reservation.resources.ReservationMapper;
import org.esgi.reservation.resources.dto.in.ReservationRequest;
import org.esgi.reservation.resources.dto.in.ReservationUpdateRequest;
import org.esgi.users.Role;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

    @InjectMocks
    ReservationService service;

    @Mock ReservationRepository reservationRepo;
    @Mock UserRepository        userRepo;
    @Mock ParkingSlotRepository slotRepo;
    @Mock ReservationMapper     mapper;

    UUID userId, slotId;
    UserEntity user;
    ParkingSlotEntity slot;

    @BeforeEach
    void init() {
        userId = UUID.randomUUID();
        slotId = UUID.randomUUID();

        user = new UserEntity();
        user.id = userId;
        user.role = Role.EMPLOYEE;
        user.isHybridOrElectric = false;

        slot = new ParkingSlotEntity();
        slot.id = slotId;
        slot.row = "B";
        slot.number = 1;
        slot.code = "B01";
        slot.has_charger = false;
    }


    @Nested class CreateReservation {

        @Test
        void ok_when_data_valid_and_no_overlap() {
            ReservationRequest rq = validRequest(LocalDate.now().plusDays(1));

            mockBasics();
            when(reservationRepo.existsBySlotAndDateRange(any(), any(), any())).thenReturn(false);
            when(mapper.fromRequest(eq(rq), eq(user), eq(slot))).thenReturn(new ReservationEntity());

            assertDoesNotThrow(() -> service.createReservation(rq));
            verify(reservationRepo).persist(any(ReservationEntity.class));
        }

        @Test
        void fail_when_dates_empty() {
            ReservationRequest rq = new ReservationRequest();
            rq.userId = userId; rq.slotId = slotId; rq.dates = Collections.emptyList();

            mockBasics();
            var ex = assertThrows(RuntimeException.class, () -> service.createReservation(rq));
            assertTrue(ex.getMessage().contains("cannot be empty"));
        }

        @Test
        void fail_when_too_many_days_for_employee() {
            ReservationRequest rq = validRequest();
            rq.dates = generateDates(6);      // EMPLOYEE max 5

            mockBasics();
            var ex = assertThrows(RuntimeException.class, () -> service.createReservation(rq));
            assertTrue(ex.getMessage().contains("Too many"));
        }

        @Test
        void fail_when_past_date() {
            ReservationRequest rq = validRequest(LocalDate.now().minusDays(1));

            mockBasics();
            assertThrows(RuntimeException.class, () -> service.createReservation(rq));
        }

        @Test
        void fail_when_vehicle_not_compatible() {
            slot.row = "A";                   // rangée électrique
            ReservationRequest rq = validRequest();

            mockBasics();
            assertThrows(RuntimeException.class, () -> service.createReservation(rq));
        }

        @Test
        void fail_when_overlap_exists() {
            ReservationRequest rq = validRequest();
            mockBasics();
            when(reservationRepo.existsBySlotAndDateRange(any(), any(), any())).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> service.createReservation(rq));
        }
    }


    @Nested class UpdateReservation {

        @Test
        void ok_update_existing() {
            UUID resId = UUID.randomUUID();
            ReservationEntity existing = new ReservationEntity();
            existing.id = resId;
            existing.userId = userId;
            existing.slot = slot;

            ReservationUpdateRequest upd = new ReservationUpdateRequest();
            upd.startDateTime = LocalDate.now().plusDays(3);
            upd.endDateTime   = upd.startDateTime.plusDays(1);

            when(reservationRepo.findById(resId)).thenReturn(existing);
            when(userRepo.findById(userId)).thenReturn(user);

            assertDoesNotThrow(() -> service.updateReservation(resId, upd));
            verify(mapper).applyUpdate(existing, upd);
            verify(reservationRepo).persist(existing);
        }

        @Test
        void fail_when_reservation_not_found() {
            UUID resId = UUID.randomUUID();
            when(reservationRepo.findById(resId)).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> service.updateReservation(resId, new ReservationUpdateRequest()));
        }

        @Test
        void fail_when_invalid_range() {
            UUID resId = UUID.randomUUID();
            ReservationEntity existing = new ReservationEntity();
            existing.id = resId; existing.userId = userId; existing.slot = slot;
            when(reservationRepo.findById(resId)).thenReturn(existing);
            when(userRepo.findById(userId)).thenReturn(user);

            ReservationUpdateRequest upd = new ReservationUpdateRequest();
            upd.startDateTime = LocalDate.now().plusDays(3);
            upd.endDateTime   = upd.startDateTime.minusDays(1);   // start> end

            assertThrows(RuntimeException.class, () -> service.updateReservation(resId, upd));
        }
    }


    @Nested class DeleteReservation {

        @Test
        void ok_delete() {
            UUID resId = UUID.randomUUID();
            when(reservationRepo.findById(resId)).thenReturn(new ReservationEntity());

            assertDoesNotThrow(() -> service.deleteReservation(resId));
            verify(reservationRepo).delete(any(ReservationEntity.class));
        }

        @Test
        void fail_when_not_found() {
            UUID resId = UUID.randomUUID();
            when(reservationRepo.findById(resId)).thenReturn(null);
            assertThrows(RuntimeException.class, () -> service.deleteReservation(resId));
        }
    }

    // ---------------------------------------------------------------------
    // 4. checkIn
    // ---------------------------------------------------------------------
    @Nested class CheckIn {

        @Test
        void ok_same_slot() {
            ReservationEntity res = new ReservationEntity();
            res.slot = slot;

            when(reservationRepo.findReservationOfTheDay(userId)).thenReturn(res);

            assertDoesNotThrow(() -> service.checkIn(userId, slotId));
            assertNotNull(res.checkedInAt);
        }

        @Test
        void fail_wrong_slot() {
            ReservationEntity res = new ReservationEntity();
            res.slot = slot;
            when(reservationRepo.findReservationOfTheDay(userId)).thenReturn(res);

            assertThrows(IllegalArgumentException.class,
                    () -> service.checkIn(userId, UUID.randomUUID()));
        }
    }


    @Nested class ReleaseUnattended {

        @Test
        void truncate_and_create_tail_when_multi_day() {
            LocalDate today = LocalDate.now();
            LocalDateTime eleven = today.atTime(11, 0);

            ReservationEntity r = new ReservationEntity();
            r.slot = slot;
            r.userId = userId;
            r.startDate = today;
            r.endDate = today.plusDays(2);
            r.checkedInAt = null;

            when(reservationRepo.findUnattendedReservations()).thenReturn(List.of(r));

            ArgumentCaptor<ReservationEntity> capt = ArgumentCaptor.forClass(ReservationEntity.class);
            doNothing().when(reservationRepo).persist(capt.capture());

            service.releaseUnattendedReservations();

            assertEquals(eleven.toLocalDate(), r.endDate);

            ReservationEntity tail = capt.getValue();
            assertEquals(today.plusDays(1), tail.startDate);
            assertEquals(today.plusDays(2), tail.endDate);
            assertSame(slot, tail.slot);
        }
    }

    @Nested class Queries {

        @Test
        void list_by_slot() {
            when(reservationRepo.findAllBySlotId(slotId)).thenReturn(List.of());
            when(mapper.toResponseList(any())).thenReturn(List.of());

            service.getAllReservationsBySlotId(slotId);

            verify(reservationRepo).findAllBySlotId(slotId);
            verify(mapper).toResponseList(any());
        }

        @Test
        void list_all() {
            when(reservationRepo.listAll()).thenReturn(List.of());
            when(mapper.toResponseList(any())).thenReturn(List.of());

            service.getAllReservations();
            verify(reservationRepo).listAll();
            verify(mapper).toResponseList(any());
        }
    }


    private ReservationRequest validRequest(LocalDate... customDates) {
        ReservationRequest rq = new ReservationRequest();
        rq.userId = userId;
        rq.slotId = slotId;
        rq.dates = customDates.length == 0
                ? List.of(LocalDate.now().plusDays(1))
                : Arrays.asList(customDates);
        return rq;
    }

    private List<LocalDate> generateDates(int nDays) {
        LocalDate start = LocalDate.now().plusDays(1);
        List<LocalDate> out = new ArrayList<>();
        for (int i=0;i<nDays;i++) out.add(start.plusDays(i));
        return out;
    }

    private void mockBasics() {
        when(userRepo.findById(userId)).thenReturn(user);
        when(slotRepo.findById(slotId)).thenReturn(slot);
    }
}
