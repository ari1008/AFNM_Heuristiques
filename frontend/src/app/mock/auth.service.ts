import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  sessionToken: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  private _isLoggedIn = new BehaviorSubject<boolean>(!!localStorage.getItem('session'));
  public isLoggedIn$ = this._isLoggedIn.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    const loginRequest: LoginRequest = { email, password };
    return new Observable(observer => {
      this.http.post<LoginResponse>(this.apiUrl + "/login", loginRequest).subscribe({
        next: (res) => {
          localStorage.setItem('session', res.sessionToken);
          localStorage.setItem('email', res.email);
          localStorage.setItem('role', res.role);
          this._isLoggedIn.next(true);
          observer.next(res);
          observer.complete();
        },
        error: err => observer.error(err)
      });
    });
  }

  logout(): Observable<void> {
    const token = localStorage.getItem('session');
    if (!token) {
      this.clearSession();
      return new Observable(observer => observer.complete());
    }

    const headers = new HttpHeaders().set('Authorization', token);
    return new Observable(observer => {
      this.http.post<void>(this.apiUrl + "/logout", {}, { headers }).subscribe({
        next: () => {
          this.clearSession();
          observer.next();
          observer.complete();
        },
        error: () => {
          this.clearSession();
          observer.complete();
        }
      });
    });
  }

  clearSession(): void {
    localStorage.clear();
    this._isLoggedIn.next(false);
  }
}
