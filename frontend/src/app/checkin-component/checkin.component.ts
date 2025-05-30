import { Component, OnInit } from '@angular/core';
import {
  FormBuilder, FormGroup, Validators,
  ReactiveFormsModule, FormsModule
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';

import { ReservationService } from '../service/reservation.service';
import { ParkingService } from '../service/parking.service';
import { Slot } from '../model/parking.model';

@Component({
  selector: 'app-checkin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './checkin.component.html',
  styleUrls: ['./checkin.component.css']
})
export class CheckinComponent implements OnInit {
  checkinForm: FormGroup;
  message: string | null = null;
  error: string | null = null;

  allSlots: Slot[] = [];

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private parkingService: ParkingService,
    private router: Router
  ) {
    this.checkinForm = this.fb.group({
      slotId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.parkingService.getAll().subscribe({
      next: (slots) => {
        this.allSlots = slots;
      },
      error: (err) => {
        this.error = err.message;
      }
    });
  }

  submit(): void {
    this.message = null;
    this.error = null;

    const slotId = this.checkinForm.value.slotId;
    const userId = localStorage.getItem('id');

    if (!userId) {
      this.error = 'Utilisateur non authentifié.';
      return;
    }

    this.reservationService.checkIn(userId, slotId).subscribe({
      next: () => {
        this.message = 'Check-in effectué avec succès !';
        this.checkinForm.reset();
        setTimeout(() => this.router.navigate(['/']), 1500);
      },
      error: (err) => this.error = err.message
    });
  }
}
