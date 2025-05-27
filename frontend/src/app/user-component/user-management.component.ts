import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../model/user.model';
import { UserService } from '../mock/user.service';

@Component({
  standalone: true,
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];

  newUser: User = {
    email: '',
    password: '',
    role: 'user'
  };

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.refreshUsers();
  }

  refreshUsers(): void {
    this.users = this.userService.getUsers();
  }

  addUser(): void {
    if (!this.newUser.email || !this.newUser.password) {
      alert('Email et mot de passe requis.');
      return;
    }

    const alreadyExists = this.users.some(u => u.email === this.newUser.email);
    if (alreadyExists) {
      alert('Un utilisateur avec cet email existe déjà.');
      return;
    }

    this.userService.addUser({ ...this.newUser });
    this.newUser = { email: '', password: '', role: 'user' };
    this.refreshUsers();
  }

  deleteUser(email: string): void {
    this.userService.deleteUser(email);
    this.refreshUsers();
  }
}
