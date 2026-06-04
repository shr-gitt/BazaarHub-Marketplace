import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ChartModule } from 'primeng/chart';
import { CardModule } from 'primeng/card';
import { OrderItemResponse } from '../../../core/models/order.model';
import { OrderService } from '../../../services/order.service';
import { ProductService } from '../../../services/product.service';
import { VendorContextService } from '../../../services/vendor-context.service';

@Component({
  selector: 'app-vendor-dashboard',
  standalone: true,
  imports: [CommonModule, ChartModule, CardModule],
  templateUrl: './vendor-dashboard.component.html',
  styleUrl: './vendor-dashboard.component.scss',
})
export class VendorDashboardComponent implements OnInit {
  orders: OrderItemResponse[] = [];
  isLoading = true;

  totalRevenue = 0;
  thisMonthRevenue = 0;
  totalOrders = 0;
  totalProducts = 0;

  revenueByMonth: any;
  ordersByStatus: any;
  topProducts: any;
  ordersByDay: any;

  chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
  };

  constructor(
    private orderService: OrderService,
    private productService: ProductService,
    private vendorContext: VendorContextService,
  ) {}

  ngOnInit(): void {
    const vendorId = this.vendorContext.getVendorId()!;

    forkJoin({
      orders: this.orderService.getVendorOrders(vendorId, 0, 1000),
      products: this.productService.getByVendorId(vendorId, 0, 1000),
    }).subscribe({
      next: ({ orders, products }) => {
        this.orders = orders.data?.content || [];
        this.totalProducts = products.data?.totalElements || 0;
        this.computeStats();
        this.buildCharts();
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      },
    });
  }

  computeStats(): void {
    const now = new Date();
    this.totalOrders = this.orders.length;
    this.totalRevenue = this.orders.reduce((s, o) => s + o.totalAmount, 0);
    this.thisMonthRevenue = this.orders
      .filter((o) => {
        const d = new Date(o.createdAt!);
        return (
          d.getMonth() === now.getMonth() &&
          d.getFullYear() === now.getFullYear()
        );
      })
      .reduce((s, o) => s + o.totalAmount, 0);
  }

  buildCharts(): void {
    const statusCount: Record<string, number> = {};
    this.orders.forEach((o) => {
      statusCount[o.orderStatus] = (statusCount[o.orderStatus] || 0) + 1;
    });
    this.ordersByStatus = {
      labels: Object.keys(statusCount),
      datasets: [
        {
          data: Object.values(statusCount),
          backgroundColor: [
            '#f59e0b',
            '#6366f1',
            '#10b981',
            '#ef4444',
            '#3b82f6',
            '#ec4899',
          ],
        },
      ],
    };

    const revenueMap: Record<string, number> = {};
    this.orders.forEach((o) => {
      const month = new Date(o.createdAt!).toLocaleString('default', {
        month: 'short',
        year: '2-digit',
      });
      revenueMap[month] = (revenueMap[month] || 0) + o.totalAmount;
    });
    this.revenueByMonth = {
      labels: Object.keys(revenueMap),
      datasets: [
        {
          label: 'Revenue (Rs.)',
          data: Object.values(revenueMap),
          fill: true,
          borderColor: '#6366f1',
          backgroundColor: 'rgba(99,102,241,0.1)',
          tension: 0.4,
        },
      ],
    };

    const productCount: Record<string, number> = {};
    this.orders.forEach((o) => {
      productCount[o.productName] = (productCount[o.productName] || 0) + 1;
    });
    const sorted = Object.entries(productCount)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5);
    this.topProducts = {
      labels: sorted.map(([name]) => name),
      datasets: [
        {
          label: 'Units Sold',
          data: sorted.map(([, c]) => c),
          backgroundColor: '#6366f1',
          borderRadius: 6,
        },
      ],
    };

    const dayMap: Record<string, number> = {};
    const now = new Date();
    this.orders
      .filter((o) => {
        const d = new Date(o.createdAt!);
        return (
          d.getMonth() === now.getMonth() &&
          d.getFullYear() === now.getFullYear()
        );
      })
      .forEach((o) => {
        const day = new Date(o.createdAt!).getDate().toString();
        dayMap[day] = (dayMap[day] || 0) + 1;
      });
    this.ordersByDay = {
      labels: Object.keys(dayMap).map((d) => `Day ${d}`),
      datasets: [
        {
          label: 'Orders',
          data: Object.values(dayMap),
          backgroundColor: '#10b981',
          borderRadius: 6,
        },
      ],
    };
  }
}
