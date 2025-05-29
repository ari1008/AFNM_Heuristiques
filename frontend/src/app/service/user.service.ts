import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {CreateUserRequest, UserForSecretary, UserUpdateRequest} from '../model/register.model';


@Injectable({providedIn: 'root'})
export class UserService {
  private apiUrl = 'http://localhost:8080/admin/users';

  constructor(private http: HttpClient) {
  }


  getAll(): Observable<UserForSecretary[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserForSecretary[]>(this.apiUrl, {headers})
      .pipe(
        catchError(error => {
          console.error('Error fetching users:', error);
          throw error;
        })
      );
  }

  update(id: string, userData: Partial<UserUpdateRequest>): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.put(`${this.apiUrl}/${id}`, userData, {headers})
      .pipe(
        catchError(error => {
          console.error('Error updating user:', error);
          throw error;
        })
      );
  }

  delete(id: string): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.delete(`${this.apiUrl}/${id}`, {headers})
      .pipe(
        catchError(error => {
          console.error('Error deleting user:', error);
          throw error;
        })
      );
  }


  createUser(
    firstname: string,
    lastname: string,
    email: string,
    password: string,
    role: string,
    isElectricOrHybrid: boolean
  ): Observable<CreateUserRequest> {
    const headers = this.getAuthHeaders();
    const createUserRequest: CreateUserRequest = {
      firstname,
      lastname,
      email,
      password,
      role,
      isElectricOrHybrid
    };

    return this.http.post<CreateUserRequest>(this.apiUrl, createUserRequest, {headers}).pipe(
      map(response => response),


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
}
