import { Injectable } from '@angular/core';
import { User } from '../model/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private users: User[] = [
    { email: 'alice@company.com', role: 'user' },
    { email: 'bob@company.com', role: 'secretary' },
    { email: 'manager@company.com', role: 'manager' }
  ];

  getUsers(): User[] {
    return [...this.users];
  }

  addUser(user: User): void {
    this.users.push(user);
  }

  deleteUser(email: string): void {
    this.users = this.users.filter(u => u.email !== email);
  }
}
