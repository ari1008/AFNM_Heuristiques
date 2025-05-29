import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';

interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  id: string;
  email: string;
  role: string;
  firstname: string;
  lastname: string;
  isElectricOrHybrid: boolean
}

interface LoginResponse extends AuthUser {
  sessionToken: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  private _isLoggedIn = new BehaviorSubject<boolean>(!!localStorage.getItem('session'));
  public isLoggedIn$ = this._isLoggedIn.asObservable();

  private userSubject = new BehaviorSubject<AuthUser | null>(this.getUserFromStorage());
  public currentUser = this.userSubject.asObservable();

  constructor(private http: HttpClient) {
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const loginRequest: LoginRequest = {email, password};
    return new Observable(observer => {
      this.http.post<LoginResponse>(this.apiUrl + "/login", loginRequest).subscribe({
        next: (res) => {
          localStorage.setItem('session', res.sessionToken);
          localStorage.setItem('email', res.email);
          localStorage.setItem('role', res.role);
          localStorage.setItem('id', res.id);
          localStorage.setItem('firstname', res.firstname);
          localStorage.setItem('lastname', res.lastname);
          localStorage.setItem('isElectricOrHybrid',JSON.stringify(res.isElectricOrHybrid))

          const user: AuthUser = {
            email: res.email,
            role: res.role,
            id: res.id,
            firstname: res.firstname,
            lastname: res.lastname,
            isElectricOrHybrid: res.isElectricOrHybrid
          };
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

    const headers = token
      ? new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Token ${token}`
      })
      : undefined;

    return new Observable(observer => {
      this.http.post<void>(this.apiUrl + "/logout", {}, {headers}).subscribe({
        next: () => {
          this.clearSession();
          observer.next();
          observer.complete();
        },
        error: () => {
          this.clearSession();
          observer.next();
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
    const session = localStorage.getItem('session');
    const id = localStorage.getItem('id');
    const firstname = localStorage.getItem('firstname');
    const lastname = localStorage.getItem('lastname');
    const isElectricOrHybrid = localStorage.getItem('isElectricOrHybrid');
    if (email && role && session && id && firstname && lastname && isElectricOrHybrid) {
      const parsedValueIsElectricOrHybrid: boolean = isElectricOrHybrid ? JSON.parse(isElectricOrHybrid) : false;
      return {
        id: id, email: email, role: role, firstname: firstname, lastname: lastname, isElectricOrHybrid: parsedValueIsElectricOrHybrid
      }
    }
    return null;
  }
}
