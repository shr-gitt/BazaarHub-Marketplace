import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { VendorService } from '../../../services/vendor.service';
import { AddressComponent } from '../../../shared/components/address/address.component';
import { Router } from '@angular/router';
import { VendorContextService } from '../../../services/vendor-context.service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-vendor-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AddressComponent,
    ToastModule,
    ButtonModule,
  ],
  templateUrl: './vendor-profile.component.html',
  styleUrls: ['./vendor-profile.component.scss'],
  providers: [MessageService],
})
export class VendorProfileComponent implements OnInit {
  @Input() isUpdate = false;

  form: FormGroup;
  loading = false;
  saving = false;

  constructor(
    private fb: FormBuilder,
    private vendorService: VendorService,
    private router: Router,
    private messageService: MessageService,
    private vendorContext: VendorContextService,
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
        wardNo: [null, [Validators.required, Validators.min(1)]],
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
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Vendor not found.',
      });
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
        }

        this.loading = false;

        setTimeout(() => {
          if (res.data?.address) {
            const addr = res.data.address;
            const addrGroup = this.form.get('addressRequestDto');

            addrGroup?.patchValue({
              wardNo: addr.wardNo,
              street: addr.street,
              postalCode: addr.postalCode,
            });

            addrGroup?.get('province')?.setValue(addr.province);
            addrGroup?.get('district')?.setValue(addr.district);
            addrGroup?.get('municipality')?.setValue(addr.municipality);
          }
        }, 0);
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }

  onSubmit(): void {
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      return;
    }

    this.saving = true;

    const vendorId = this.getVendorId();

    if (!this.isUpdate) {
      this.vendorService.create(this.form.value).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Vendor profile submitted for approval.',
          });
          this.saving = false;
          this.router.navigate(['/vendor/selection']);
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail:
              err?.error?.message || 'Something went wrong, try again later.',
          });
          this.saving = false;
        },
      });

      return;
    }

    if (!vendorId) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Vendor not found.',
      });
      this.saving = false;
      return;
    }

    this.vendorService.update(vendorId, this.form.value).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Vendor profile updated.',
        });
        this.saving = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
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
