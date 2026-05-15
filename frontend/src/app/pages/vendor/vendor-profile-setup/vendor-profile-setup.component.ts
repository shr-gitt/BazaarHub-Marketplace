import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { AddressComponent } from '../../../shared/components/address/address.component';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-vendor-profile-setup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, AddressComponent],
  templateUrl: './vendor-profile-setup.component.html',
  styleUrl: './vendor-profile-setup.component.scss',
})
export class VendorProfileSetupComponent {
  profileForm: FormGroup;
  loading = false;
  error = '';
  success = '';

  private baseUrl = 'http://localhost:8080/api';
  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
  ) {
    this.profileForm = this.fb.group({
      shopName: ['', Validators.required],
      businessEmail: ['', [Validators.required, Validators.email]],
      businessPhone: ['', Validators.required],
      panCardNo: ['', Validators.required],
      registrationNo: ['', Validators.required],

      addressRequestDto: this.fb.group({
        province: ['', Validators.required],
        district: ['', Validators.required],
        municipality: ['', Validators.required],
        wardNo: ['', Validators.required],
        street: ['', Validators.required],
        postalCode: ['', Validators.required],
      }),
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';
    this.http
      .post(`${this.baseUrl}/vendor/create`, this.profileForm.value)
      .subscribe({
        next: () => {
          this.loading = false;
          this.success =
            'Vendor profile created successfully. Redirecting to login...';
          localStorage.clear();
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        },
        error: (err) => {
          console.log('VENDOR PROFILE ERROR:', err);
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

  get addressForm(): FormGroup {
    return this.profileForm.get('addressRequestDto') as FormGroup;
  }
}
