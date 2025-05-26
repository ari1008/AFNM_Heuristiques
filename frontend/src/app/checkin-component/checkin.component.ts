import {Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import {ReservationService, Reservation} from '../mock/reservation.service';

@Component({
  standalone: true,
  selector: 'app-checkin',
  imports: [CommonModule],
  templateUrl: './checkin.component.html',
  styleUrls: []
})
export class CheckinComponent implements OnInit {
  slotId: string | null = null;
  userEmail: string = '';
  reservation: Reservation | null = null;
  message: string = '';

  constructor(
    private route: ActivatedRoute,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    this.slotId = this.route.snapshot.paramMap.get('slotId');
    this.userEmail = localStorage.getItem('email') || '';
    this.findTodayReservation();
  }

  findTodayReservation(): void {
    const today = new Date().toISOString().split('T')[0];
    const reservations = this.reservationService.getReservationsForSlot(this.slotId!);
    const found = reservations.find(r =>
      r.startDate === today &&
      r.reservedBy === this.userEmail &&
      r.status === 'active'
    );
    if (found) {
      this.reservation = found;
      this.message = `Réservation trouvée pour aujourd'hui.`;
    } else {
      this.message = `Aucune réservation trouvée pour aujourd'hui.`;
    }
  }

  checkIn(): void {
    if (this.reservation) {
      this.reservation.checkedIn = true;
      this.message = '✅ Check-in confirmé !';
    }
  }
}

