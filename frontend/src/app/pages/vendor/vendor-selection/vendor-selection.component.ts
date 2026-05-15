import { Component, OnInit } from '@angular/core';
import { Vendor } from '../../../core/models/vendor.model';
import { VendorService } from '../../../services/vendor.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { VendorContextService } from '../../../services/vendor-context.service';

@Component({
  selector: 'app-vendor-selection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vendor-selection.component.html',
  styleUrl: './vendor-selection.component.scss',
})
export class VendorSelectionComponent implements OnInit {
  loading = false;
  saving = false;
  error = '';
  success = '';
  vendors: Vendor[] = [];

  constructor(
    private vendorService: VendorService,
    private auth: AuthService,
    private router: Router,
    private vendorContext: VendorContextService,
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const userId = this.auth.getUserId();
    if (!userId) return;

    this.loading = true;

    this.vendorService.getByUser().subscribe({
      next: (res) => {
        this.vendors = res.data ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  selectVendor(vendor: Vendor): void {
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
