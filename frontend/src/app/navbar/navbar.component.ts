import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, AuthUser } from '../auth-component/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;
  userEmail = '';
  role = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe((user: AuthUser | null) => {
      this.isLoggedIn = !!user;
      this.userEmail = user?.email || '';
      this.role = user?.role || '';
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

  createAccount(): void {
    this.router.navigate(['/register']);
  }
}
