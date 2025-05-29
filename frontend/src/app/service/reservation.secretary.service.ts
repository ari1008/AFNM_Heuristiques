import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ReservationRequest, ReservationResponse, ReservationUpdateRequest } from '../model/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationSecretaryService {
  private readonly apiUrl = 'http://localhost:8080/admin/reservations';

  constructor(private http: HttpClient) {}

  getAllReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError('récupération des réservations'))
    );
  }

  createReservation(userId: string, dates: string[], slotId: string): Observable<void> {
    const reservation: ReservationRequest = { userId, dates, slotId };
    return this.http.post<void>(this.apiUrl, reservation, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError('création de réservation'))
    );
  }

  updateReservation(id: string, startDateTime: string, endDateTime: string): Observable<void> {
    const update: ReservationUpdateRequest = { startDateTime, endDateTime };
    return this.http.put<void>(`${this.apiUrl}/${id}`, update, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError('modification de réservation'))
    );
  }

  deleteReservation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError('suppression de réservation'))
    );
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('session');
    if (!token) {
      throw new Error('Token d\'authentification non trouvé');
    }

    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Token ${token}`
    });
  }

  private handleError(context: string) {
    return (error: HttpErrorResponse): Observable<never> => {
      console.error(`Erreur lors de la ${context}:`, error);

      let message = 'Une erreur est survenue. Veuillez réessayer.';

      switch (error.status) {
        case 401:
          message = 'Session expirée. Veuillez vous reconnecter.';
          break;
        case 403:
          message = 'Accès interdit. Vous n’avez pas les droits nécessaires.';
          break;
        case 404:
          message = 'La ressource demandée est introuvable.';
          break;
        case 400:
          message = error.error?.message ?? 'Requête invalide.';
          break;
      }

      return throwError(() => new Error(message));
    };
  }
}
