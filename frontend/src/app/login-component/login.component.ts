import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../mock/auth.service';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private router: Router,
              private authService: AuthService
  ) {
  }

  login() {
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        localStorage.setItem('session', res.sessionToken.toString());
        localStorage.setItem('email', res.email);
        localStorage.setItem('role', res.role);
        this.router.navigate(['/']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.errorMessage = "Email ou mot de passe incorrect";
        } else {
          this.errorMessage = "Erreur de connexion. Veuillez réessayer plus tard.";
        }
      }
    });
  }
}
