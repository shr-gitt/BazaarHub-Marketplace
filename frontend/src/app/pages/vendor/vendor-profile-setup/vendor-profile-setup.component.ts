import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AddressComponent } from '../../../shared/components/address/address.component';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { VendorService } from '../../../services/vendor.service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-vendor-profile-setup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AddressComponent,
    ToastModule,
    ButtonModule,
  ],
  templateUrl: './vendor-profile-setup.component.html',
  styleUrl: './vendor-profile-setup.component.scss',
  providers: [MessageService],
})
export class VendorProfileSetupComponent {
  profileForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
    private vendorService: VendorService,
    private messageService: MessageService,
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
        wardNo: [null, [Validators.required, Validators.min(1)]],
        street: ['', Validators.required],
        postalCode: ['', Validators.required],
      }),
    });
  }

  onSubmit(): void {
    this.profileForm.markAllAsTouched();

    if (this.profileForm.invalid) {
      return;
    }

    this.loading = true;
    this.vendorService.create(this.profileForm.value).subscribe({
      next: () => {
        this.auth.logout();
        this.loading = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Vendor profile created successfully.',
        });
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || 'Failed to create vendor profile.',
        });
      },
    });
  }

  get addressForm(): FormGroup {
    return this.profileForm.get('addressRequestDto') as FormGroup;
  }
}
