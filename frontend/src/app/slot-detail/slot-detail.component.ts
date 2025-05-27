import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';

import { ReservationService, Reservation } from '../mock/reservation.service';

@Component({
  standalone: true,
  selector: 'app-slot-detail',
  imports: [
    CommonModule,
    FormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatNativeDateModule,
    MatInputModule
  ],
  templateUrl: './slot-detail.component.html',
  styleUrls: []
})
export class SlotDetailComponent implements OnInit {
  slotId: string | null = null;
  role: 'admin' | 'user' | 'secretary' = 'user';
  userEmail: string = '';

  selectedStartDate: Date | null = null;
  selectedEndDate: Date | null = null;

  reservations: Reservation[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    this.slotId = this.route.snapshot.paramMap.get('id');
    this.role = (localStorage.getItem('role') as any) || 'user';
    this.userEmail = localStorage.getItem('email') || '';

    if (this.slotId) {
      this.reservations = this.reservationService.getReservationsForSlot(this.slotId);

      if (this.isAdminOrSecretary()) {
        this.checkLateReservations();
      }
    }
  }

  isAdminOrSecretary(): boolean {
    return this.role === 'admin' || this.role === 'secretary';
  }

  maxDuration(): number {
    return this.role === 'admin' ? 30 : 5;
  }

  isEndDateValid(): boolean {
    if (!this.selectedStartDate || !this.selectedEndDate) return true;
    const diff = (this.selectedEndDate.getTime() - this.selectedStartDate.getTime()) / (1000 * 3600 * 24);
    return diff >= 0 && diff < this.maxDuration();
  }

  checkLateReservations() {
    this.reservations.forEach(r => {
      if (this.reservationService.isCheckinLate(r)) {
        r.status = 'expired';
      }
    });
  }

  checkIn(): void {
    const today = new Date().toISOString().split('T')[0];
    const r = this.reservations.find(r =>
      r.startDate === today && r.reservedBy === this.userEmail && !r.checkedIn
    );
    if (r) {
      r.checkedIn = true;
      alert('✅ Check-in effectué');
    }
  }

  submitReservation(): void {
    if (!this.slotId || !this.selectedStartDate || !this.selectedEndDate) return;

    const reservation: Reservation = {
      slotId: this.slotId,
      reservedBy: this.userEmail,
      startDate: this.selectedStartDate.toISOString().split('T')[0],
      endDate: this.selectedEndDate.toISOString().split('T')[0],
      checkedIn: false,
      status: 'active'
    };

    this.reservationService.makeReservation(reservation);
    alert('✅ Réservation effectuée !');
    this.router.navigate(['/']);
  }

  logout(): void {
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}
