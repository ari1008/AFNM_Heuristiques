import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CheckInRequest } from '../model/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly apiUrl = 'http://localhost:8080/reservations';

  constructor(private http: HttpClient) {}

  createReservation(userId: string, dates: string[], slotId: string): Observable<void> {
    const body = { userId, slotId, dates };
    const headers = this.getAuthHeaders();
    console.log(body)
    console.log(headers)
    return this.http.post<void>(this.apiUrl, body, { headers });
  }

  checkIn(userId: string, slotId: string): Observable<void> {
    const payload: CheckInRequest = { userId, slotId };
    return this.http.post<void>(`${this.apiUrl}/checkin`, payload, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError('check-in')));
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
