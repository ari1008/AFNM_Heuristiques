import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';

import { ReservationResponse } from '../model/reservation.model';
import { UserForSecretary } from '../model/register.model';
import { ReservationSecretaryService } from '../service/reservation.secretary.service';
import { UserService } from '../service/user.service';
import { ParkingService } from '../service/parking.service';
import { Slot } from '../model/parking.model';

@Component({
  selector: 'app-reservation-secretary',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatTableModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatSelectModule
  ],
  templateUrl: './admin.reservation.component.html',
  styleUrls: ['./admin-reservation.component.css']
})
export class AdminReservationComponent implements OnInit {
  reservationForm: FormGroup;
  users: UserForSecretary[] = [];
  reservations: ReservationResponse[] = [];
  slots: Slot[] = [];
  editingId: string | null = null;
  displayedColumns: string[] = ['userId', 'slotId', 'start', 'end', 'actions'];

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationSecretaryService,
    private userService: UserService,
    private parkingService: ParkingService
  ) {
    this.reservationForm = this.fb.group({
      userId: ['', Validators.required],
      slotId: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    this.loadReservations();
    this.loadSlots();
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (users) => this.users = users,
      error: (err) => console.error('Erreur lors du chargement des utilisateurs', err)
    });
  }

  loadReservations(): void {
    this.reservationService.getAllReservations().subscribe({
      next: (reservations) => this.reservations = reservations,
      error: (err) => console.error('Erreur lors du chargement des réservations', err)
    });
  }

  loadSlots(): void {
    this.parkingService.getAll().subscribe({
      next: (slots) => this.slots = slots,
      error: (err) => console.error('Erreur lors du chargement des slots', err)
    });
  }

  createReservation(): void {
    const { userId, slotId, startDate, endDate } = this.reservationForm.value;
    this.reservationService.createReservation(userId, [startDate, endDate], slotId).subscribe({
      next: () => {
        this.loadReservations();
        this.reservationForm.reset();
      },
      error: (err) => console.error('Erreur lors de la création', err)
    });
  }

  enableEdit(reservation: ReservationResponse): void {
    this.editingId = reservation.id;
  }

  updateReservation(reservation: ReservationResponse): void {
    this.reservationService.updateReservation(reservation.id, reservation.startDate, reservation.endDate).subscribe({
      next: () => {
        this.editingId = null
        this.parkingService.refreshCache()
      },
      error: (err) => console.error('Erreur lors de la mise à jour', err)
    });
  }

  deleteReservation(id: string): void {
    this.reservationService.deleteReservation(id).subscribe({
      next: () => this.loadReservations(),
      error: (err) => console.error('Erreur lors de la suppression', err)
    });
  }

  getUserEmail(userId: string): string {
    const user = this.users.find(u => u.id === userId);
    return user ? `${user.email}` : 'Inconnu';
  }

  getSlotCode(slotId: string): string {
    const slot = this.slots.find(s => s.id === slotId);
    return slot ? slot.code : 'Inconnu';
  }

  formatDate(date: string): string {
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }
}
