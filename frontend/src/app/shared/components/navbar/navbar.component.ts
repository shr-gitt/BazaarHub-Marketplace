import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

interface NavItem {
  label: string;
  //icon: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent implements OnInit {
  username: string | null = null;
  role: string | null = null;

  navItems: NavItem[] = [
    // Admin
    { label: 'Admin Dashboard', route: '/admin/dashboard', roles: ['ADMIN'] },
    { label: 'Users', route: '/admin/users', roles: ['ADMIN'] },
    { label: 'Vendor Approval', route: '/admin/vendors', roles: ['ADMIN'] },

    // Vendor
    {
      label: 'Vendor Dashboard',
      route: '/vendor/dashboard',
      roles: ['VENDOR'],
    },

    // Customer
    { label: 'Home', route: '/customer/dashboard', roles: ['CUSTOMER'] },
    { label: 'My Cart', route: '/cart', roles: ['CUSTOMER'] },

    // Shared
    {
      label: 'Products',
      route: '/products',
      roles: ['ADMIN', 'VENDOR', 'CUSTOMER'],
    },
    { label: 'Categories', route: '/categories', roles: ['ADMIN'] },
    {
      label: 'Orders',
      route: '/orders',
      roles: ['ADMIN', 'VENDOR', 'CUSTOMER'],
    },
    {
      label: 'Notifications',
      route: '/notifications',
      roles: ['ADMIN', 'VENDOR', 'CUSTOMER'],
    },

    { label: 'My Profile', route: '/profile', roles: ['VENDOR'] },
    { label: 'My Profile', route: '/profile', roles: ['CUSTOMER'] },
  ];

  constructor(
    public auth: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.role = this.auth.getRole();
  }

  get filteredNav(): NavItem[] {
    return this.navItems.filter(
      (item) => this.role && item.roles.includes(this.role),
    );
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
