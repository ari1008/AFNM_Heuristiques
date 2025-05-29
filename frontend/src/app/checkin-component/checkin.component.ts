import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';

import { ReservationService } from '../service/reservation.service';

@Component({
  selector: 'app-checkin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './checkin.component.html',
  styleUrls: ['./checkin.component.css']
})
export class CheckinComponent {
  checkinForm: FormGroup;
  message: string | null = null;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private router: Router
  ) {
    this.checkinForm = this.fb.group({
      slotId: ['', Validators.required]
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
