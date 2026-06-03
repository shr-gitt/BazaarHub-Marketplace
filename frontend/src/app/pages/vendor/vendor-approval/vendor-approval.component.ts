import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VendorService } from '../../../services/vendor.service';
import { VendorResponse } from '../../../core/models/vendor.model';
import { ApprovalStatus } from '../../../core/models/enums.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-vendor-approval',
  standalone: true,
  imports: [
    CommonModule,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ButtonModule,
  ],
  templateUrl: './vendor-approval.component.html',
  styleUrls: ['./vendor-approval.component.scss'],
})
export class VendorApprovalComponent implements OnInit {
  vendors: VendorResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;

  constructor(private vendorService: VendorService) {}

  ngOnInit() {
    this.loadVendors(0);
  }

  loadVendors(page: number) {
    this.isLoading = true;
    this.vendorService.getAll(page, 20).subscribe({
      next: (res) => {
        if (res.data) {
          this.vendors = res.data.content.filter(
            (vendor) => vendor.approvalStatus === 'PENDING',
          );
          this.currentPage = res.data.number;
          this.totalPages = res.data.totalPages;
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  updateStatus(id: number, status: string) {
    if (confirm(`Are you sure you want to mark this vendor as ${status}?`)) {
      this.vendorService
        .approve(id, { approvalStatus: status as ApprovalStatus })
        .subscribe({
          next: () => {
            this.loadVendors(this.currentPage);
          },
          error: (err) => {
            alert(err.error?.message || 'Failed to update vendor status.');
          },
        });
    }
  }
}
