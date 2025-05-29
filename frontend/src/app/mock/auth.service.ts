import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  email: string;
  role: string;
}

interface LoginResponse extends AuthUser {
  sessionToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  private _isLoggedIn = new BehaviorSubject<boolean>(!!localStorage.getItem('session'));
  public isLoggedIn$ = this._isLoggedIn.asObservable();

  private userSubject = new BehaviorSubject<AuthUser | null>(this.getUserFromStorage());
  public currentUser = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    const loginRequest: LoginRequest = { email, password };
    return new Observable(observer => {
      this.http.post<LoginResponse>(this.apiUrl + "/login", loginRequest).subscribe({
        next: (res) => {
          localStorage.setItem('session', res.sessionToken);
          localStorage.setItem('email', res.email);
          localStorage.setItem('role', res.role);

          const user: AuthUser = { email: res.email, role: res.role };
          this.userSubject.next(user);
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

  private clearSession(): void {
    localStorage.clear();
    this._isLoggedIn.next(false);
    this.userSubject.next(null);
  }

  private getUserFromStorage(): AuthUser | null {
    const email = localStorage.getItem('email');
    const role = localStorage.getItem('role');
    if (email && role) {
      return { email, role };
    }
    return null;
  }
}
