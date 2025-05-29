import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ReservationRequest {
  userId: string;
  dates: string[];
  slotId: string;
  period: 'AM' | 'PM';
}
/*
 "slot": {
            "id": "5585b067-c78b-44cf-b28a-1012d08aab0d",
            "code": "11",
            "row": "A",
            "number": 2,
            "hasCharger": true
        },
 */
interface Slot {
  id: string,
  code: string,
  row: string,
  number: number,
  hasCharger: boolean,
}
interface Reservation {
  id: string;
  userId: string;
  slotId: Slot;
  startDateTime: string;
  endDateTime: string;
}

interface ApiReservationResponse {
  id: string;
  userId: string;
  slot: Slot;
  startDateTime: string;
  endDateTime: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReservationServiceTwo {
  private apiUrl = 'http://localhost:8080/reservations';

  constructor(private http: HttpClient) {}

  getAllReservations(): Observable<ApiReservationResponse[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<ApiReservationResponse[]>(this.apiUrl, { headers });
  }

  createReservation(reservationData: ReservationRequest): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post<any>(this.apiUrl, reservationData, { headers });
  }


  makeReservation(dates: Date[], slotId: string, period: 'AM' | 'PM'): Observable<any> {

    const userId = localStorage.getItem('userId');

    if (!userId) {
      throw new Error('Utilisateur non connecté');
    }

    const formattedDates = dates.map(date => this.formatDate(date));


    const reservationData: ReservationRequest = {
      userId,
      dates: formattedDates,
      slotId,
      period
    };

    return this.createReservation(reservationData);
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
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
}
