import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatNativeDateModule} from '@angular/material/core';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {ReservationServiceTwo} from '../mock/reservation.service.two';
import {Reservation} from '../mock/Reservation';


@Component({
  standalone: true,
  selector: 'app-slot-detail',
  imports: [
    CommonModule,
    FormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatNativeDateModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './slot-detail.component.html',
  styleUrls: []
})
export class SlotDetailComponent implements OnInit {
  slotId: string | null = null;
  role: 'admin' | 'user' | 'secretary' = 'user';
  userEmail: string = '';
  userId: string = '';

  today: Date = new Date();

  selectedDate: Date | null = null;
  selectedDates: Date[] = [];
  selectedPeriod: 'AM' | 'PM' = 'AM';

  periods = [
    {value: 'AM', label: 'Matin (8h00 - 12h00)'},
    {value: 'PM', label: 'Après-midi (13h00 - 18h00)'}
  ];

  // Utiliser l'interface Reservation de votre service
  apiReservations: Reservation[] = [];
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private reservationService: ReservationServiceTwo
  ) {
  }

  ngOnInit(): void {
    this.slotId = this.route.snapshot.paramMap.get('id');
    this.role = (localStorage.getItem('role') as any) || 'user';
    this.userEmail = localStorage.getItem('email') || '';
    this.userId = localStorage.getItem('userId') || '';

    if (this.slotId) {
      // Charger les réservations depuis l'API
      this.loadReservations();
    }
  }

  loadReservations(): void {
    this.isLoading = true;
    this.reservationService.getAllReservations().subscribe({
      next: (data) => {
        console.log(`this is data ${Object.keys(data).length}`);
        if (data.length > 0) {
          console.log(`this is data ${data[0].userId}`);
        }

        // Filtrer les réservations pour ce slot
        this.apiReservations = data
          .filter((r) => this.verification(r.slot.number, r.slot.row))
          .map((r) => {
            return {
              slotId: {
                number: r.slot.number,
                row: r.slot.row
              },
              reservedBy: r.userId,
              startDate: new Date(r.startDateTime),
              endDate: new Date(r.endDateTime)
            };
          });
        this.isLoading = false;
      },
      error: (err) => {
        if (err.status === 401) {
          this.navigateToLogin();
        } else {
          this.errorMessage = 'Erreur lors du chargement des réservations';
        }
        this.isLoading = false;
      }
    });
  }

  isAdminOrSecretary(): boolean {
    return this.role === 'admin' || this.role === 'secretary';
  }

  /**
   * Ajoute une date à la liste des dates sélectionnées
   */
  addDate(): void {
    if (!this.selectedDate) {
      this.errorMessage = 'Veuillez sélectionner une date';
      return;
    }

    // Vérifier que la date n'est pas déjà dans la liste
    const dateString = this.formatDate(this.selectedDate);
    if (this.selectedDates.some(d => this.formatDate(d) === dateString)) {
      this.errorMessage = 'Cette date est déjà sélectionnée';
      return;
    }

    this.selectedDates.push(new Date(this.selectedDate));
    this.selectedDate = null;
    this.errorMessage = '';
  }

  /**
   * Supprime une date de la liste des dates sélectionnées
   */
  removeDate(index: number): void {
    this.selectedDates.splice(index, 1);
  }

  /**
   * Formate une date au format YYYY-MM-DD
   */
  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Soumet une réservation à l'API
   */
  submitApiReservation(): void {
    if (!this.slotId) {
      this.errorMessage = 'ID d\'emplacement non valide';
      return;
    }

    if (this.selectedDates.length === 0) {
      this.errorMessage = 'Veuillez sélectionner au moins une date';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.reservationService.makeReservation(
      this.selectedDates,
      this.slotId,
      this.selectedPeriod
    ).subscribe({
      next: () => {
        this.successMessage = '✅ Réservation effectuée !';
        this.selectedDates = [];
        this.loadReservations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401) {
          this.navigateToLogin();
        } else {
          this.errorMessage = err.error?.message || err.error || 'Erreur lors de la création de la réservation';
        }
      }
    });
  }

  navigateToHome(): void {
    this.router.navigate(['/'])
      .catch(err => console.error('Erreur de navigation vers l\'accueil:', err));
  }

  navigateToLogin(): void {
    this.router.navigate(['/login'])
      .catch(err => console.error('Erreur de navigation vers la page de connexion:', err));
  }

  verification(numberSlot: number, letterSlot: string): boolean {
    console.log(numberSlot)
    console.log(letterSlot)
    if (!this.slotId) {
      return false;
    }

    let formattedSlotId: string;

    if (numberSlot > 9) {
      formattedSlotId = letterSlot + numberSlot.toString();
    } else {
      formattedSlotId = letterSlot + "0" + numberSlot.toString();
    }

    return formattedSlotId === this.slotId;
  }

  logout(): void {
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('session');
    window.location.href = '/login';
  }
}
