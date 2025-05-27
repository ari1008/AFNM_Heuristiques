// ✅ parking-lot.component.ts (mis à jour avec login/logout complets)
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReservationService } from '../mock/reservation.service';

@Component({
  selector: 'app-parking-lot',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './parking-lot.component.html',
  styleUrls: ['./parking-lot.component.css']
})
export class ParkingLotComponent {
  rows = ['A', 'B', 'C', 'D', 'E', 'F'];
  columns = Array.from({ length: 10 }, (_, i) => i + 1);

  role: string | null = null;
  userEmail: string | null = null;

  constructor(private reservationService: ReservationService) {
    this.role = localStorage.getItem('role');
    this.userEmail = localStorage.getItem('email');
  }

  getSlot(row: string, col: number) {
    const id = `${row}${col.toString().padStart(2, '0')}`;
    return { id, occupied: this.isSlotReservedToday(id) };
  }

  isSlotReservedToday(slotId: string): boolean {
    const today = new Date().toISOString().split('T')[0];
    const reservations = this.reservationService.getReservationsForSlot(slotId);
    return reservations.some(r =>
      r.startDate <= today && r.endDate >= today && r.status === 'active'
    );
  }

  isElectricRow(row: string): boolean {
    return row === 'A' || row === 'F';
  }

  isLoggedIn(): boolean {
    return !!this.userEmail;
  }

  redirectToLogin(): void {
    window.location.href = '/login';
  }

  logout(): void {
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}
