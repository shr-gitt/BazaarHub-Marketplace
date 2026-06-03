import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { OrderItemResponse } from '../../../core/models/order.model';
import { OrderService } from '../../../services/order.service';
import { VendorContextService } from '../../../services/vendor-context.service';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  Validators,
} from '@angular/forms';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-vendor-orders',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    TagModule,
    ButtonModule,
    DropdownModule,
    FormsModule,
    ToastModule,
  ],
  templateUrl: './vendor-orders.component.html',
  styleUrl: './vendor-orders.component.scss',
  providers: [MessageService],
})
export class VendorOrdersComponent implements OnInit {
  orders: OrderItemResponse[] = [];
  isLoading = true;
  vendorId: number | null = null;
  currentPage = 0;
  pageSize = 10;
  totalRecords = 0;
  checkoutForm: FormGroup;

  orderStatusOptions = [
    { label: 'Pending', value: 'PENDING' },
    { label: 'Packaging', value: 'PACKAGING' },
    { label: 'Packed', value: 'PACKED' },
    { label: 'Shipped', value: 'SHIPPED' },
    { label: 'Delivered', value: 'DELIVERED' },
    { label: 'Cancelled', value: 'CANCELLED' },
  ];

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private messageService: MessageService,
    private vendorContextService: VendorContextService,
  ) {
    this.checkoutForm = this.fb.group({
      orderStatus: ['', Validators.required],
    });

    this.checkoutForm.valueChanges.subscribe(() => {});
  }

  ngOnInit(): void {
    this.loadOrders(0);
  }
  loadOrders(page: number): void {
    this.isLoading = true;

    this.vendorId = this.vendorContextService.getVendorId();

    this.orderService
      .getVendorOrders(this.vendorId, page, this.pageSize)
      .subscribe({
        next: (res) => {
          this.orders = res.data?.content || [];
          this.currentPage = res.data?.number || 0;
          this.totalRecords = res.data?.totalElements || 0;
          this.isLoading = false;
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Order fetching failed.',
            detail: err.errror?.message || 'Failed to load orders.',
          });
          this.isLoading = false;
        },
      });
  }
  onStatusChange(order: any, event: any): void {
    const newStatus = event.value;
    const previousStatus = order.orderStatus;

    this.orderService
      .updateStatus(order.id, { orderStatus: newStatus })
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Status Updated',
            detail: `Order #${order.id} marked as ${newStatus}`,
          });
        },
        error: () => {
          order.orderStatus = previousStatus;
          this.messageService.add({
            severity: 'error',
            summary: 'Update Failed',
            detail: 'Could not update order status.',
          });
        },
      });
  }
  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.pageSize = event.rows;
    this.loadOrders(page);
  }
}
