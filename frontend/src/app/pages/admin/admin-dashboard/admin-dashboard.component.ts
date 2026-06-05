import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { forkJoin } from 'rxjs';
import { ChartModule } from 'primeng/chart';
import { OrderService } from '../../../services/order.service';
import { UserService } from '../../../services/user.service';
import { VendorService } from '../../../services/vendor.service';
import { OrderResponse } from '../../../core/models/order.model';
import { DecimalPipe, CommonModule } from '@angular/common';
import { ApprovalStatus } from '../../../core/models/enums.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    CardModule,
    ButtonModule,
    ChartModule,
    DecimalPipe,
    CommonModule,
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
})
export class AdminDashboardComponent implements OnInit {
  isLoading = true;

  totalRevenue = 0;
  thisMonthRevenue = 0;
  totalOrders = 0;
  totalUsers = 0;
  totalVendors = 0;
  pendingVendors = 0;

  revenueByMonth: any;
  ordersByStatus: any;
  topVendors: any;
  userGrowth: any;

  chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
  };

  horizontalBarOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
    indexAxis: 'y' as const,
  };

  constructor(
    private orderService: OrderService,
    private userService: UserService,
    private vendorService: VendorService,
  ) {}

  ngOnInit(): void {
    forkJoin({
      orders: this.orderService.getAdminOrders(0, 1000),
      users: this.userService.getAll(0, 1000),
      vendors: this.vendorService.getAll(0, 1000),
    }).subscribe({
      next: ({ orders, users, vendors }) => {
        const allOrders: OrderResponse[] = orders.data?.content || [];
        const allUsers = users.data?.content || [];
        const allVendors = vendors.data?.content || [];

        this.totalOrders = allOrders.length;
        this.totalUsers = users.data?.totalElements || 0;
        this.totalVendors = vendors.data?.totalElements || 0;
        this.pendingVendors = allVendors.filter(
          (v: any) => v.approvalStatus === 'PENDING',
        ).length;

        const now = new Date();
        this.totalRevenue = allOrders.reduce((s, o) => s + o.totalAmount, 0);
        this.thisMonthRevenue = allOrders
          .filter((o) => {
            const d = new Date(o.createdAt!);
            return (
              d.getMonth() === now.getMonth() &&
              d.getFullYear() === now.getFullYear()
            );
          })
          .reduce((s, o) => s + o.totalAmount, 0);

        this.buildCharts(allOrders, allUsers);
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      },
    });
  }

  buildCharts(orders: OrderResponse[], users: any[]): void {
    const statusCount: Record<string, number> = {};
    orders.forEach((o) => {
      const status = o.orderStatus || (o as any).status || 'Unknown';
      statusCount[status] = (statusCount[status] || 0) + 1;
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
    orders.forEach((o) => {
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

    const vendorRevenue: Record<string, number> = {};
    orders.forEach((o) => {
      const key = (o as any).vendorName || (o as any).vendorId || 'Unknown';
      vendorRevenue[key] = (vendorRevenue[key] || 0) + o.totalAmount;
    });
    const sortedVendors = Object.entries(vendorRevenue)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5);
    this.topVendors = {
      labels: sortedVendors.map(([name]) => name),
      datasets: [
        {
          label: 'Revenue (Rs.)',
          data: sortedVendors.map(([, v]) => v),
          backgroundColor: '#6366f1',
          borderRadius: 6,
        },
      ],
    };

    const userMap: Record<string, number> = {};
    users.forEach((u) => {
      const createdAt = u.createdAt || u.created_at;
      if (!createdAt) return;
      const month = new Date(createdAt).toLocaleString('default', {
        month: 'short',
        year: '2-digit',
      });
      userMap[month] = (userMap[month] || 0) + 1;
    });
    this.userGrowth = {
      labels: Object.keys(userMap),
      datasets: [
        {
          label: 'New Users',
          data: Object.values(userMap),
          backgroundColor: '#10b981',
          borderRadius: 6,
        },
      ],
    };
  }
}
