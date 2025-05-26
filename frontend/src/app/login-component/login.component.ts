import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Connexion</h2>
    <form (ngSubmit)="login()">
      <label>Email :</label>
      <input type="email" [(ngModel)]="email" name="email" required /><br />
      <label>Mot de passe :</label>
      <input type="password" [(ngModel)]="password" name="password" required /><br />
      <button type="submit">Se connecter</button>
    </form>
    <p *ngIf="errorMessage" style="color:red;">{{ errorMessage }}</p>
  `,
  styles: []
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private router: Router) {}

  login() {
    if (this.password !== 'admin') {
      this.errorMessage = 'Mot de passe incorrect';
      return;
    }

    localStorage.setItem('token', 'fake-token');
    localStorage.setItem('email', this.email);
    if (this.email === 'admin@site.com') {
      localStorage.setItem('role', 'admin');
    } else if (this.email === 'secretary@site.com') {
      localStorage.setItem('role', 'secretary');
    } else {
      localStorage.setItem('role', 'user');
    }

    this.router.navigate(['/']);
  }
}
