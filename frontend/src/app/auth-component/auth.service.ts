import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  isLoggedIn(): boolean {
    return !!localStorage.getItem('session');
  }

  getToken(): string | null {
    return localStorage.getItem('session');
  }

  logout(): void {
    localStorage.removeItem('session');
  }
}
