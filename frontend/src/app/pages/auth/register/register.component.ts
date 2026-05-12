import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  error = '';
  success = '';

  roles = ['CUSTOMER', 'VENDOR'];

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
  ) {
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
  get f() {
    return this.registerForm.controls;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.error = '';
    this.success = '';

    this.auth.register(this.registerForm.value).subscribe({
      next: (res) => {
        this.loading = false;
        this.success =
          'Account created successfully. Complete your profile setup.';
        const selectedRole = this.registerForm.value.role;
        if (selectedRole === 'CUSTOMER') {
          this.router.navigate(['/customer/profile-setup']);
        } else if (selectedRole === 'VENDOR') {
          this.router.navigate(['/vendor/profile-setup']);
        }
      },
      error: (err) => {
        console.log('REGISTER ERROR:', err);

        this.loading = false;
        this.error = this.getErrorMessage(err);
      },
    });
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
