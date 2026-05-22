import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { MessageModule } from 'primeng/message';
import { DividerModule } from 'primeng/divider';

import { UserService } from '../../../services/user.service';
import { UserRequest } from '../../../core/models/user.model';

@Component({
  selector: 'app-create-admin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    SelectModule,
    MessageModule,
    DividerModule,
  ],
  templateUrl: './create-admin.component.html',
  styleUrl: './create-admin.component.scss',
})
export class CreateAdminComponent {
  adminForm: FormGroup;
  loading = false;
  error = '';
  success = '';

  genderOptions = [
    { label: 'Male', value: 'MALE' },
    { label: 'Female', value: 'FEMALE' },
    { label: 'Other', value: 'OTHER' },
  ];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
  ) {
    this.adminForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', Validators.required],
      gender: ['MALE', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  onSubmit(): void {
    if (this.adminForm.invalid) {
      this.adminForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const dto: UserRequest = {
      ...this.adminForm.value,
      role: 'ADMIN',
    };

    this.userService.createAdmin(dto).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Admin created successfully.';

        setTimeout(() => {
          this.router.navigate(['/admin/dashboard']);
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Failed to create admin.';
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  isInvalid(controlName: string): boolean {
    const control = this.adminForm.get(controlName);
    return !!(control && control.touched && control.invalid);
  }
}
