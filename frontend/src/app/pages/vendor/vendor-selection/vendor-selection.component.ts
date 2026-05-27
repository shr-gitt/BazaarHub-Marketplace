import { Component, OnInit } from '@angular/core';
import { VendorService } from '../../../services/vendor.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { VendorContextService } from '../../../services/vendor-context.service';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { VendorResponse } from '../../../core/models/vendor.model';

@Component({
  selector: 'app-vendor-selection',
  standalone: true,
  imports: [CommonModule, ButtonModule, ToastModule],
  templateUrl: './vendor-selection.component.html',
  styleUrls: ['./vendor-selection.component.scss'],
  providers: [MessageService],
})
export class VendorSelectionComponent implements OnInit {
  loading = false;
  vendors: VendorResponse[] = [];

  constructor(
    private vendorService: VendorService,
    private auth: AuthService,
    private router: Router,
    private vendorContext: VendorContextService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const userId = this.auth.getUserId();
    if (!userId) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'User session not found. Please log in again.',
      });
      return;
    }

    this.loading = true;

    this.vendorService.getByUser().subscribe({
      next: (res) => {
        this.vendors = res.data ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load vendors. Please try again.',
        });
        return;
      },
    });
  }

  selectVendor(vendor: VendorResponse): void {
    this.vendorContext.setVendorId(vendor.id);

    this.router.navigate(['/vendor/dashboard']);
  }

  onCreateVendor(): void {
    this.router.navigate(['/vendor/profile-setup'], {
      state: { isUpdate: false },
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
