import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {User} from '../model/user.model';
import {UserService} from '../services/user.service';

@Component({
  standalone: true,
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html'
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  newUser: User = { email: '', role: 'user' };

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.refreshUsers();
  }

  refreshUsers(): void {
    this.users = this.userService.getUsers();
  }

  addUser(): void {
    if (this.newUser.email) {
      this.userService.addUser({ ...this.newUser });
      this.newUser.email = '';
      this.refreshUsers();
    }
  }

  deleteUser(email: string): void {
    this.userService.deleteUser(email);
    this.refreshUsers();
  }
}
