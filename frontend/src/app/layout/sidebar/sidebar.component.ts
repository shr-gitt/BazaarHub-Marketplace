import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {
  role: string | null = null;
  id: number | null = null;
  collapsed = false;

  navItems: NavItem[] = [
    // Admin
    {
      label: 'Admin Dashboard',
      icon: '🏛️',
      route: '/admin/dashboard',
      roles: ['ADMIN'],
    },
    { label: 'Users', icon: '👥', route: '/admin/users', roles: ['ADMIN'] },
    {
      label: 'Vendor Approval',
      icon: '✅',
      route: '/admin/vendors',
      roles: ['ADMIN'],
    },

    // Vendor
    {
      label: 'Vendor Dashboard',
      icon: '🏪',
      route: '/vendor/dashboard',
      roles: ['VENDOR'],
    },
    {
      label: 'My Profile',
      icon: '🏢',
      route: '/vendor/profile',
      roles: ['VENDOR'],
    },

    // Customer
    {
      label: 'Home',
      icon: '🏠',
      route: '/customer/dashboard',
      roles: ['CUSTOMER'],
    },
    { label: 'My Profile', icon: '👤', route: '/profile', roles: ['CUSTOMER'] },
    { label: 'My Cart', icon: '🛒', route: '/cart', roles: ['CUSTOMER'] },

    // Shared
    {
      label: 'Products',
      icon: '📦',
      route: '/products',
      roles: ['ADMIN', 'VENDOR', 'CUSTOMER'],
    },
    { label: 'Categories', icon: '🗂️', route: '/categories', roles: ['ADMIN'] },
    {
      label: 'Orders',
      icon: '📋',
      route: '/orders',
      roles: ['ADMIN', 'VENDOR', 'CUSTOMER'],
    },
    {
      label: 'Notifications',
      icon: '🔔',
      route: '/notifications',
      roles: ['ADMIN', 'VENDOR', 'CUSTOMER'],
    },
  ];

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.role = this.auth.getRole();
    this.id = this.auth.getUserId();
  }

  get filteredNav(): NavItem[] {
    return this.navItems.filter(
      (item) => this.role && item.roles.includes(this.role),
    );
  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
