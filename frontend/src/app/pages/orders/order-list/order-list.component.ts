import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { AuthService } from '../../../services/auth.service';
import { OrderResponse } from '../../../core/models/order.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss'],
})
export class OrderListComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = true;
  isCustomer = false;
  isVendor = false;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.isCustomer = this.authService.isCustomer();
    this.isVendor = this.authService.isVendor();
    this.loadOrders();
  }

  loadOrders() {
    this.isLoading = true;
    this.orderService.getMyOrders().subscribe({
      next: (res) => {
        if (res.data) {
          this.orders = res.data.sort(
            (a, b) =>
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
          );
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  cancelOrder(id: number) {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.orderService.cancelOrder(id).subscribe({
        next: () => {
          this.loadOrders();
        },
        error: (err) => {
          alert(err.error?.message || 'Failed to cancel order.');
        },
      });
    }
  }

  updateStatus(id: number, status: string) {
    this.orderService
      .updateStatus(id, { orderStatus: status as any })
      .subscribe({
        next: () => {
          this.loadOrders();
        },
        error: (err) => {
          alert(err.error?.message || 'Failed to update order status.');
        },
      });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS':
      case 'DELIVERED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'CANCELLED':
      case 'FAILED':
        return 'error';
      default:
        return 'info';
    }
  }

  navigateHome() {
    window.location.href = '/products';
  }
}
