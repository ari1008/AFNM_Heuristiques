import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import {ReservationService, Reservation} from '../mock/reservation.service';


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
  selectedStartDate: Date | null = null;
  selectedEndDate: Date | null = null;
  userEmail: string = '';


  reservations: Reservation[] = [];

  constructor(
    private route: ActivatedRoute,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    this.slotId = this.route.snapshot.paramMap.get('id');
    const role = localStorage.getItem('role');
    this.role = (role === 'admin' || role === 'secretary') ? role : 'user';
    this.userEmail = localStorage.getItem('email') || '';

    if (this.slotId && this.isAdminOrSecretary()) {
      this.reservations = this.reservationService.getReservationsForSlot(this.slotId);
    }
    if (this.slotId) {
      this.reservations = this.reservationService.getReservationsForSlot(this.slotId);
      if (this.role === 'admin' || this.role === 'secretary') {
        this.checkLateReservations();
      }
    }

  }

  checkIn() {
    const today = new Date().toISOString().split('T')[0];
    const r = this.reservations.find(r =>
      r.startDate === today && r.reservedBy === this.userEmail && !r.checkedIn
    );
    if (r) r.checkedIn = true;
  }

  checkLateReservations() {
    this.reservations.forEach(r => {
      if (this.reservationService.isCheckinLate(r)) {
        r.status = 'expired';
      }
    });
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
}
