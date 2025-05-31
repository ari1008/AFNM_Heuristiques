import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, AuthUser } from '../service/auth.service';
import {ROLES} from '../model/user.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;
  firstName: string = '';
  lastName: string = '';
  userEmail = '';
  role = '';
  isSecretary = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe((user: AuthUser | null) => {
      this.isLoggedIn = !!user;
      this.firstName = user?.firstname || '';
      this.lastName = user?.lastname || '';
      this.userEmail = user?.email || '';
      this.role = user?.role || '';
      this.isSecretary = this.role === ROLES.SECRETARY.name;
      console.log('isLoggedIn:', this.isLoggedIn);
      console.log('isSecretary:', this.isSecretary);
    });
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  redirectToLogin(): void {
    this.router.navigate(['/login']);
  }

  managementUser(): void {
    this.router.navigate(['/admin/users']);
  }

  managementReservation(): void {
    this.router.navigate(['/admin/reservations'])
  }
}
