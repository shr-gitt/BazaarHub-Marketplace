import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { VendorService } from '../../../services/vendor.service';
import { AuthService } from '../../../services/auth.service';
import { AddressComponent } from '../../../shared/components/address/address.component';
import { Router } from '@angular/router';
import { Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { VendorContextService } from '../../../services/vendor-context.service';

@Component({
  selector: 'app-vendor-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AddressComponent],
  templateUrl: './vendor-profile.component.html',
  styleUrls: ['./vendor-profile.component.scss'],
})
export class VendorProfileComponent implements OnInit {
  @Input() isUpdate = false;

  form: FormGroup;
  loading = false;
  saving = false;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private vendorService: VendorService,
    private auth: AuthService,
    private router: Router,
    private vendorContext: VendorContextService,

    @Inject(PLATFORM_ID) private platformId: object,
  ) {
    this.form = this.fb.group({
      shopName: ['', Validators.required],
      panCardNo: ['', Validators.required],
      registrationNo: ['', Validators.required],
      businessEmail: ['', [Validators.required, Validators.email]],
      businessPhone: [''],
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
    if (!this.isUpdate) {
      const stateFlag = history.state?.isUpdate;
      this.isUpdate = stateFlag !== undefined ? stateFlag : false;
    }

    if (this.isUpdate) {
      this.load();
    }
  }

  private getVendorId(): number | null {
    return this.vendorContext.getVendorId();
  }

  load(): void {
    const vendorId = this.getVendorId();

    if (!vendorId) {
      this.error = 'Vendor ID not found';
      this.saving = false;
      return;
    }

    this.loading = true;

    this.vendorService.getById(vendorId).subscribe({
      next: (res) => {
        if (res.data) {
          this.form.patchValue({
            shopName: res.data.shopName,
            panCardNo: res.data.panCardNo,
            registrationNo: res.data.registrationNo,
            businessEmail: res.data.businessEmail,
            businessPhone: res.data.businessPhone,
          });

          if (res.data.addressResponseDto) {
            this.form
              .get('addressRequestDto')
              ?.patchValue(res.data.addressResponseDto);
          }
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';

    const vendorId = this.getVendorId();

    if (!this.isUpdate) {
      this.vendorService.create(this.form.value).subscribe({
        next: () => {
          this.success = 'Vendor profile submitted for approval.';
          this.saving = false;

          this.router.navigate(['/vendor/selection']);
        },
        error: (err) => {
          this.error = err?.error?.message || 'Create failed';
          this.saving = false;
        },
      });

      return;
    }

    if (!vendorId) {
      this.error = 'Vendor ID not found';
      this.saving = false;
      return;
    }

    this.vendorService.update(vendorId, this.form.value).subscribe({
      next: () => {
        this.success = 'Vendor profile updated.';
        this.saving = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Update failed';
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
