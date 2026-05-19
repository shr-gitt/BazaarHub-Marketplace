import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth-model.component.html',
  styleUrl: './auth-model.component.scss',
})
export class AuthModalComponent {
  @Input() initialMode: 'LOGIN' | 'REGISTER' = 'LOGIN';
  @Output() close = new EventEmitter<void>();

  mode: 'LOGIN' | 'REGISTER' = 'LOGIN';
  loginForm: FormGroup;
  registerForm: FormGroup;
  loading = false;
  error = '';
  success = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
  ) {
    this.mode = this.initialMode;
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });

    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required]],
      gender: ['MALE', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
      role: ['CUSTOMER', Validators.required],
    });
  }

  ngOnInit() {
    this.mode = this.initialMode;
  }

  toggleMode(newMode: 'LOGIN' | 'REGISTER') {
    this.mode = newMode;
    this.error = '';
    this.success = '';
  }

  onLogin() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = '';
    this.auth.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.close.emit();
        this.redirectByRole();
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Login failed';
      },
    });
  }

  onRegister() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = '';
    this.auth.register(this.registerForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Account created! Redirecting...';
        setTimeout(() => {
          this.close.emit();
          const role = this.registerForm.value.role;
          if (role === 'CUSTOMER')
            this.router.navigate(['/customer/profile-setup']);
          else this.router.navigate(['/vendor/profile-setup']);
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Registration failed';
      },
    });
  }

  private redirectByRole(): void {
    const role = this.auth.getRole();
    if (role === 'ADMIN') this.router.navigate(['/admin/dashboard']);
    else if (role === 'VENDOR') this.router.navigate(['/vendor/selection']);
    else this.router.navigate(['/customer/dashboard']);
  }
}
