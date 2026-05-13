import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormArray,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { CustomerService } from '../../../services/customer-profile.service';
import { AddressComponent } from '../../../shared/components/address/address.component';
import { AddressResponseDto } from '../../../core/models/address.model';

const CATEGORIES = [
  { id: 1, label: 'Electronics' },
  { id: 2, label: 'Fashion' },
  { id: 3, label: 'Home & Living' },
  { id: 4, label: 'Sports' },
  { id: 5, label: 'Books' },
  { id: 6, label: 'Beauty' },
  { id: 7, label: 'Groceries' },
  { id: 8, label: 'Toys' },
];

@Component({
  selector: 'app-customer-profile-setup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AddressComponent],
  templateUrl: './customer-profile-setup.component.html',
  styleUrls: ['./customer-profile-setup.component.scss'],
})
export class CustomerProfileSetupComponent implements OnInit {
  @Input() isUpdate = false;

  form: FormGroup;

  loading = false;
  saving = false;

  error = '';
  success = '';

  categories = CATEGORIES;

  selectedFile: File | null = null;
  previewUrl: string | null = null;

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router,
  ) {
    this.form = this.fb.group({
      dateOfBirth: ['', Validators.required],

      preferences: this.fb.array([], Validators.required),

      addressRequestDto: this.fb.group({
        province: [''],
        district: [''],
        municipality: [''],
        wardNo: [null, Validators.required],
        street: [''],
        postalCode: [''],
      }),
    });
  }

  ngOnInit(): void {
    if (this.isUpdate) {
      this.load();
    }
  }

  load(): void {
    this.loading = true;

    this.customerService.getByUser().subscribe({
      next: (res) => {
        if (res.data) {
          const {
            id,
            addressResponseDto,
            profileImageUrl,
            preferences,
            dateOfBirth,
          } = res.data;

          localStorage.setItem('customerId', String(id));

          this.form.patchValue({
            dateOfBirth,
          });

          const prefsArray = this.preferences;

          prefsArray.clear();

          (preferences || []).forEach((id: any) => {
            prefsArray.push(this.fb.control(Number(id)));
          });

          if (addressResponseDto) {
            const address: AddressResponseDto = addressResponseDto;

            this.form.get('addressRequestDto')?.patchValue(address);
          }

          if (profileImageUrl) {
            this.previewUrl = profileImageUrl;
          }
        }

        this.loading = false;
      },

      error: () => {
        this.loading = false;
      },
    });
  }

  get preferences(): FormArray {
    return this.form.get('preferences') as FormArray;
  }

  isPreferenceSelected(id: number): boolean {
    return this.preferences.controls.some((control) => control.value === id);
  }

  togglePreference(id: number): void {
    const index = this.preferences.controls.findIndex(
      (control) => control.value === id,
    );

    if (index >= 0) {
      this.preferences.removeAt(index);
    } else {
      this.preferences.push(this.fb.control(id));
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files?.length) {
      this.selectedFile = input.files[0];

      const reader = new FileReader();

      reader.onload = () => {
        this.previewUrl = reader.result as string;
      };

      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (!this.isUpdate && !this.selectedFile) {
      this.error = 'Profile image is required.';
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';

    if (this.isUpdate) {
      this.update();
    } else {
      this.create();
    }
  }

  private create(): void {
    const formData = new FormData();

    formData.append(
      'customerProfileDto',
      new Blob([JSON.stringify(this.form.value)], { type: 'application/json' }),
    );

    formData.append('file', this.selectedFile!);

    this.customerService.create(formData).subscribe({
      next: () => {
        this.success =
          'Customer profile created successfully. Redirecting to login...';

        this.saving = false;

        localStorage.clear();

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },

      error: (err) => {
        this.error =
          err?.error?.message || 'Failed to create customer profile.';

        this.saving = false;
      },
    });
  }

  private update(): void {
    console.log(this.form.value);

    const customerId = Number(localStorage.getItem('customerId'));

    this.customerService.update(customerId, this.form.value).subscribe({
      next: () => {
        this.success = 'Customer profile updated.';
        this.saving = false;
        this.isUpdate = true;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to update profile.';
        this.saving = false;
      },
    });
  }

  get f() {
    return this.form.controls;
  }

  get addressForm(): FormGroup {
    return this.form.get('addressRequestDto') as FormGroup;
  }
}
