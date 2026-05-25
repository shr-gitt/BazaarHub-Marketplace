import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { OrderResponse } from '../../../core/models/order.model';
import { OrderService } from '../../../services/order.service';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, TableModule, TagModule, ButtonModule],
  templateUrl: './admin-orders.component.html',
  styleUrl: './admin-orders.component.scss',
})
export class AdminOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 10;
  totalRecords = 0;

  constructor(private orderService: OrderService) {}

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
        console.error('Failed to load admin orders:', err);
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
