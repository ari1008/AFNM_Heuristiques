import { Component, Input, OnChanges, OnInit, SimpleChanges, ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ParkingService } from '../service/parking.service';
import { Slot } from '../model/parking.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ROLES } from '../model/user.model';
import { ReservationService } from '../service/reservation.service';

@Component({
  selector: 'app-parking-lot',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './parking-lot.component.html',
  styleUrls: ['./parking-lot.component.css']
})
export class ParkingLotComponent implements OnInit, OnChanges {
  @Input() dateRange: { start: Date, end: Date } | null = null;

  error: string | null = null;
  rows = ['A', 'B', 'C', 'D', 'E', 'F'];
  columns = Array.from({ length: 10 }, (_, i) => i + 1);
  isMineCarIsElectrified = false;
  parking: Slot[] = [];
  selectedSlotCode: string | null = null;

  constructor(
    private parkingService: ParkingService,
    private cdr: ChangeDetectorRef,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    const electricFlag = localStorage.getItem('isElectricOrHybrid');
    this.isMineCarIsElectrified = electricFlag ? JSON.parse(electricFlag) : false;

    const today = new Date();
    const localTodayStr = today.toLocaleDateString('fr-CA');
    const localToday = new Date(localTodayStr);

    this.dateRange = { start: localToday, end: localToday };

    this.parkingService.getAvailableSlots(localToday, localToday).subscribe(slots => {
      this.parking = slots;
    });
    this.cdr.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['dateRange'] && changes['dateRange'].currentValue) {
      const range = changes['dateRange'].currentValue;
      console.log('ngOnChanges triggered with date range:', range);
      this.parkingService.getAvailableSlots(range.start, range.end)
        .subscribe(slots => {
          console.log(slots)
        this.parking = slots;
      });
    }
  }

  isReserved(slotId: string): boolean {
    return !this.parking.some(slot => slot.code === slotId);
  }

  getSlotId(row: string, col: number): string {
    return `${row}${col.toString().padStart(2, '0')}`;
  }

  getSlotBlockReason(row: string, col: number): Observable<string | null> {
    const id = this.getSlotId(row, col);

    return this.parkingService.getByCode(id).pipe(
      map(slot => {
        if (!slot) return 'Données indisponibles';
        if (slot.has_charger && !this.isMineCarIsElectrified) return 'Véhicule non électrifié';
        return null;
      })
    );
  }

  isElectricSlot(row: string, col: number): Observable<boolean> {
    const id = this.getSlotId(row, col);
    return this.parkingService.getByCode(id).pipe(
      map(slot => slot?.has_charger ?? false)
    );
  }

  selectSlot(code: string): void {
    this.selectedSlotCode = code;
  }

  submitReservation(): void {
    this.error = null;

    if (!this.selectedSlotCode || !this.dateRange) {
      this.error = 'Veuillez d’abord sélectionner une place et une période.';
      return;
    }

    this.parkingService.getByCode(this.selectedSlotCode).subscribe(slot => {
      if (!slot) {
        this.error = 'Impossible de retrouver l’identifiant de la place.';
        return;
      }

      const slotId = slot.id;
      const userId = localStorage.getItem('id');
      const role = (localStorage.getItem('role') ?? '').toUpperCase();
      const start = this.dateRange!.start;
      const end = this.dateRange!.end;
      const durationDays = this.countWorkingDays(start, end);

      if (!userId || !role) {
        alert('Utilisateur non authentifié');
        return;
      }

      if (end < start) {
        this.error = 'La date de fin doit être après la date de début.';
        return;
      }

      if ((role === ROLES.EMPLOYEE.name || role === ROLES.SECRETARY.name) && durationDays > 5) {
        this.error = 'Les employés peuvent réserver pour 5 jours ouvrés maximum.';
        return;
      }

      if (role === ROLES.MANAGER.name && durationDays > 30) {
        this.error = 'Les managers peuvent réserver jusqu’à 30 jours maximum.';
        return;
      }

      const startStr = start.toLocaleDateString('fr-CA');
      const endStr = end.toLocaleDateString('fr-CA');

      this.reservationService.createReservation(userId, [startStr, endStr], slotId).subscribe({
        next: () => {
          alert('Réservation réussie');
          this.selectedSlotCode = null;

          this.parkingService.getAvailableSlots(start, end).subscribe(updatedSlots => {
            this.parking = updatedSlots;
          });
        },
        error: (err) => {
          this.error = err.message;
        }
      });
    });
  }

  private countWorkingDays(start: Date, end: Date): number {
    let count = 0;
    const current = new Date(start);

    while (current <= end) {
      const day = current.getDay();
      if (day !== 0 && day !== 6) count++;
      current.setDate(current.getDate() + 1);
    }
    return count;
  }

}
