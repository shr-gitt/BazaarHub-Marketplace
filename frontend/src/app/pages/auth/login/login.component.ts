import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  FormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  error = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(16),
        ],
      ],
    });

    if (this.auth.isLoggedIn()) this.redirectByRole();
  }
  get email() {
    return this.loginForm.get('email')!;
  }
  get password() {
    return this.loginForm.get('password')!;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = '';

    this.auth.login(this.loginForm.value).subscribe({
      next: (res) => {
        this.loading = false;
        this.redirectByRole();
      },
      error: (err) => {
        console.log('LOGIN ERROR:', err);

        this.loading = false;
        this.error = this.getErrorMessage(err);
      },
    });
  }
  private redirectByRole(): void {
    const role = this.auth.getRole();
    if (role === 'ADMIN') this.router.navigate(['/admin/dashboard']);
    else if (role === 'VENDOR') this.router.navigate(['/vendor/selection']);
    else this.router.navigate(['/customer/dashboard']);
  }

  private getErrorMessage(err: any): string {
    return (
      err?.error?.message ||
      err?.error?.data?.message ||
      err?.message ||
      'Something went wrong. Please try again.'
    );
  }
}
