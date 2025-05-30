package org.esgi.parking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSlotServiceUnitTest {

    @InjectMocks
    ParkingSlotService service;

    @Mock
    ParkingSlotRepository repo;


    @Test
    void findAvailable_returns_slots() {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to   = from.plusDays(1);
        List<ParkingSlotEntity> expected = List.of(new ParkingSlotEntity());
        when(repo.findAvailableSlots(from, to)).thenReturn(expected);

        List<ParkingSlotEntity> result = service.findAvailable(from, to);
        assertSame(expected, result);
        verify(repo).findAvailableSlots(from, to);
    }

    @Test
    void findAvailable_empty_list() {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to   = from.plusDays(1);
        when(repo.findAvailableSlots(from, to)).thenReturn(Collections.emptyList());

        List<ParkingSlotEntity> result = service.findAvailable(from, to);
        assertTrue(result.isEmpty());
    }


    @Test
    void findAllSlots_returns_all() {
        List<ParkingSlotEntity> all = List.of(new ParkingSlotEntity(), new ParkingSlotEntity());
        when(repo.findAllSlots()).thenReturn(all);

        List<ParkingSlotEntity> result = service.findAllSlots();
        assertEquals(2, result.size());
        verify(repo).findAllSlots();
    }
}
