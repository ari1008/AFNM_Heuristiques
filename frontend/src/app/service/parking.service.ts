import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, map} from 'rxjs/operators';
import {BehaviorSubject, Observable, of, throwError} from 'rxjs';
import {Slot} from '../model/parking.model';

@Injectable({
  providedIn: 'root'
})
export class ParkingService {
  private readonly apiUrl = 'http://localhost:8080/slots';
  private cacheTime = 15 * 60 * 1000;
  private cachedSlots: Slot[] | null = null;
  private cachedSlotsMap: Map<string, Slot> | null = null;
  private lastCacheTime = 0;
  private refreshing = false;
  private refreshSubject = new BehaviorSubject<Slot[]>([]);
  private refreshMapSubject = new BehaviorSubject<Map<string, Slot>>(new Map());

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<Slot[]> {
    const now = Date.now();

    if (this.cachedSlots && (now - this.lastCacheTime < this.cacheTime)) {
      return of(this.cachedSlots);
    }

    if (this.refreshing) {
      return this.refreshSubject.asObservable();
    }

    return this.fetchAndCacheSlots();
  }

  getAllAsMap(): Observable<Map<string, Slot>> {
    const now = Date.now();

    if (this.cachedSlotsMap && (now - this.lastCacheTime < this.cacheTime)) {
      return of(this.cachedSlotsMap);
    }

    if (this.refreshing) {
      return this.refreshMapSubject.asObservable();
    }

    return this.fetchAndCacheSlots().pipe(
      map(() => this.cachedSlotsMap!)
    );
  }

  getByCode(code: string): Observable<Slot | undefined> {
    return this.getAllAsMap().pipe(
      map(slotsMap => slotsMap.get(code))
    );
  }

  refreshCache(): Observable<Slot[]> {
    this.cachedSlots = null;
    this.cachedSlotsMap = null;
    this.lastCacheTime = 0;
    return this.fetchAndCacheSlots();
  }

  private fetchAndCacheSlots(): Observable<Slot[]> {
    this.refreshing = true;

    this.http.get<Slot[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError('récupération des slots'))
    ).subscribe({
      next: (data) => {
        this.cachedSlots = data;

        // Création du dictionnaire (Map)
        this.cachedSlotsMap = new Map();
        for (const slot of data) {
          this.cachedSlotsMap.set(slot.code, slot);
        }

        this.lastCacheTime = Date.now();
        this.refreshSubject.next(data);
        this.refreshMapSubject.next(this.cachedSlotsMap);
        this.refreshing = false;
      },
      error: (err) => {
        this.refreshing = false;
        this.refreshSubject.error(err);
        this.refreshMapSubject.error(err);
      }
    });

    return this.refreshSubject.asObservable();
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
          message = 'Accès interdit. Vous n\'avez pas les droits nécessaires.';
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
