import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService, Reservation } from '../mock/reservation.service';

@Component({
  standalone: true,
  selector: 'app-reservation-admin',
  imports: [CommonModule],
  templateUrl: './reservation-admin.component.html',
  styleUrls: []
})
export class ReservationAdminComponent implements OnInit {
  reservations: Reservation[] = [];

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.reservations = this.reservationService.getAllHistory();
  }
}

