import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  firstname = '';
  lastname = '';
  email = '';
  password = '';
  role = 'user';
  isElectricOrHybrid = false;
  errorMessage = '';
  successMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  register() {
    const payload = {
      firstname: this.firstname,
      lastname: this.lastname,
      email: this.email,
      password: this.password,
      role: this.role,
      isElectricOrHybrid: this.isElectricOrHybrid,
    };

    this.http.post('http://localhost:8080/users', payload).subscribe({
      next: (res) => {
        this.successMessage = 'Compte créé avec succès 🎉';
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.successMessage = '';
        if (err.status === 400) {
          this.errorMessage = err.error;
        } else {
          this.errorMessage = "Une erreur est survenue. Veuillez réessayer.";
        }
      }
    });
  }
}
