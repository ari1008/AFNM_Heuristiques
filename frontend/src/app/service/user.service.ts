import { Injectable } from '@angular/core';
import { User } from '../model/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private users: User[] = [
    { email: 'alice@company.com', role: 'user' , password: 'admin'},
    { email: 'bob@company.com', role: 'secretary', password: 'admin' },
    { email: 'manager@company.com', role: 'manager', password: 'admin' }
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

  findUserByEmail(email: string): User | undefined {
    console.log(this.users)
    return this.users.find(u => u.email === email);
  }
}
