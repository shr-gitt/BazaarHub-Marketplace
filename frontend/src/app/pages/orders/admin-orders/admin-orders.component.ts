import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { OrderResponse } from '../../../core/models/order.model';
import { OrderService } from '../../../services/order.service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, TableModule, TagModule, ButtonModule, ToastModule],
  templateUrl: './admin-orders.component.html',
  styleUrl: './admin-orders.component.scss',
  providers: [MessageService],
})
export class AdminOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 10;
  totalRecords = 0;

  constructor(
    private orderService: OrderService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadOrders(0);
  }
  loadOrders(page: number): void {
    this.isLoading = true;

    this.orderService.getAdminOrders(page, this.pageSize).subscribe({
      next: (res) => {
        this.orders = res.data?.content || [];
        this.currentPage = res.data?.number || 0;
        this.totalRecords = res.data?.totalElements || 0;
        this.isLoading = false;
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Creating admin failed.',
          detail: err.error?.message || 'Failed to load admin orders.',
        });
        this.isLoading = false;
      },
    });
  }
  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.pageSize = event.rows;
    this.loadOrders(page);
  }

  getProductNames(order: OrderResponse): string {
    return order.items.map((item) => item.productName).join(', ');
  }
}
