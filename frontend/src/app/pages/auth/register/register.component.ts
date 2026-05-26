import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    ButtonModule,
    ToastModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  providers: [MessageService],
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  roles = ['CUSTOMER', 'VENDOR'];

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private messageService: MessageService,
  ) {
    this.registerForm = this.fb.group(
      {
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        phoneNumber: ['', [Validators.required]],
        gender: ['', Validators.required],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
        role: ['CUSTOMER', Validators.required],
      },
      {
        validators: this.passwordMatchValidator,
      },
    );
  }
  get f() {
    return this.registerForm.controls;
  }

  passwordMatchValidator(form: AbstractControl): ValidationErrors | null {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    this.registerForm.markAllAsTouched();

    if (this.registerForm.invalid) {
      return;
    }
    this.loading = true;

    const { confirmPassword, ...payload } = this.registerForm.value;

    this.auth.register(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Account created successfully.',
        });
        const selectedRole = this.registerForm.value.role;
        if (selectedRole === 'CUSTOMER') {
          this.router.navigate(['/customer/profile-setup']);
        } else if (selectedRole === 'VENDOR') {
          this.router.navigate(['/vendor/profile-setup'], {
            state: { isUpdate: false },
          });
        }
      },
      error: (err) => {
        console.log('REGISTER ERROR:', err);

        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Register Failed',
          detail: err?.error?.message || 'Invalid information',
        });
      },
    });
  }
}
