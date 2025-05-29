import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {ReservationService} from '../mock/reservation.service';
import {AuthService} from '../mock/auth.service';

@Component({
  selector: 'app-parking-lot',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './parking-lot.component.html',
  styleUrls: ['./parking-lot.component.css']
})
export class ParkingLotComponent implements OnInit {
  rows = ['A', 'B', 'C', 'D', 'E', 'F'];
  columns = Array.from({length: 10}, (_, i) => i + 1);

  session: string | null = null;
  role: string | null = null;
  userEmail: string | null = null;
  isLoggedIn: boolean = true;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe((logged) => {
      this.isLoggedIn = !!localStorage.getItem('session');
      this.userEmail = localStorage.getItem('email');
      this.role = localStorage.getItem('role');
    });
  }

  getSlot(row: string, col: number) {
    const id = `${row}${col.toString().padStart(2, '0')}`;
    return {id, occupied: this.isSlotReservedToday(id)};
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

  private removeAll() {
    localStorage.removeItem('session');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
  }
}
