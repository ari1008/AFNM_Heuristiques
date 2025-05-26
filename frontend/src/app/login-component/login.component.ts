import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {UserService} from '../mock/user.service'; // ✅ Ajouté

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, FormsModule], // ✅ Ajouté FormsModule ici
  templateUrl: './login.component.html',
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private router: Router, private userService: UserService) {}

  login(): void {
    const user = this.userService.findUserByEmail(this.email);
    if (!user) {
      this.errorMessage = 'Utilisateur introuvable.';
      return;
    }

    if (this.password !== user.password) {
      this.errorMessage = 'Mot de passe incorrect.';
      return;
    }

    localStorage.setItem('token', 'fake-token');
    localStorage.setItem('email', user.email);
    localStorage.setItem('role', user.role);

    this.router.navigate(['/']);
  }
}
