import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { ReservationService } from '../service/reservation.service';
import { ROLES } from '../model/user.model';
import {ParkingService} from '../service/parking.service';

@Component({
  selector: 'app-user-reservation',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './reservation-component.html',
  styleUrls: ['./reservation.component.css']
})
export class ReservationComponent implements OnInit {
  reservationForm: FormGroup;
  error: string | null = null;
  slotId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private parkingService: ParkingService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.reservationForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const code = params.get('id');

      if (code) {
        this.parkingService.getByCode(code).subscribe({
          next: (slot) => {
            if (!slot) {
              this.error = 'La place sélectionnée est introuvable.';
              return;
            }
            this.slotId = slot.id;
          },
          error: (err) => {
            this.error = err.message;
          }
        });
      } else {
        this.error = 'Aucun identifiant de place fourni dans l’URL.';
      }
    });
  }


  submit(): void {
    this.error = null;

    if (this.reservationForm.invalid || !this.slotId) {
      this.error = 'Veuillez remplir tous les champs requis.';
      return;
    }

    const { startDate, endDate } = this.reservationForm.value;
    const userId = localStorage.getItem('id');
    const role = (localStorage.getItem('role') ?? '').toUpperCase();

    if (!userId || !role) {
      alert('Utilisateur non authentifié');
      return;
    }

    const start = new Date(startDate);
    const end = new Date(endDate);
    const durationDays = this.countWorkingDays(start, end);

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

    this.reservationService.createReservation(userId, [startDate, endDate], this.slotId).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => this.error = err.message
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
