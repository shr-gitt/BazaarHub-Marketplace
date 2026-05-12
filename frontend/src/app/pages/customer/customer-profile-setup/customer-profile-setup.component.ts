import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-customer-profile-setup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './customer-profile-setup.component.html',
  styleUrl: './customer-profile-setup.component.scss',
})
export class CustomerProfileSetupComponent {
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
      dateOfBirth: ['', Validators.required],

      addressRequestDto: this.fb.group({
        province: ['', Validators.required],
        district: ['', Validators.required],
        municipality: ['', Validators.required],
        street: ['', Validators.required],
        postalCode: ['', Validators.required],
        wardNo: ['', Validators.required],
      }),

      preferences: this.fb.array([], Validators.required),
    });
  }
  get preferences(): FormArray {
    return this.profileForm.get('preferences') as FormArray;
  }
  onPreferenceChange(event: any, value: number): void {
    if (event.target.checked) {
      this.preferences.push(this.fb.control(value));
    } else {
      const index = this.preferences.controls.findIndex(
        (x) => x.value === value,
      );

      this.preferences.removeAt(index);
    }
  }
  selectedFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    if (!this.selectedFile) {
      this.error = 'Profile image is required.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const customerProfileDto = {
      dateOfBirth: this.profileForm.value.dateOfBirth,
      addressRequestDto: this.profileForm.value.addressRequestDto,
      preferences: this.profileForm.value.preferences,
    };

    const formData = new FormData();

    formData.append(
      'customerProfileDto',
      new Blob([JSON.stringify(customerProfileDto)], {
        type: 'application/json',
      }),
    );

    formData.append('file', this.selectedFile);

    this.http
      .post(`${this.baseUrl}/create-customer-profile`, formData)
      .subscribe({
        next: () => {
          this.loading = false;
          this.success =
            'Customer profile created successfully. Redirecting to login..';
          localStorage.clear();
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (err) => {
          console.log('CUSTOMER PROFILE ERROR:', err);
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
