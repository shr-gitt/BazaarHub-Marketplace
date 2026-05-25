import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { VendorContextService } from '../../../services/vendor-context.service';
import { ButtonModule } from 'primeng/button';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {
  role: string | null = null;
  id: number | null = null;
  vendorId: number | null = null;
  collapsed = false;
  sidebarVisible = true;

  navItems: NavItem[] = [
    // Admin
    {
      label: 'Admin Dashboard',
      icon: 'pi-building',
      route: '/admin/dashboard',
      roles: ['ADMIN'],
    },
    {
      label: 'Users',
      icon: 'pi-users',
      route: '/admin/users',
      roles: ['ADMIN'],
    },
    {
      label: 'Vendor Approval',
      icon: 'pi-check-circle',
      route: '/admin/vendors',
      roles: ['ADMIN'],
    },

    // Vendor
    {
      label: 'Vendor Dashboard',
      icon: 'pi-shop',
      route: '/vendor/dashboard',
      roles: ['VENDOR'],
    },
    {
      label: 'My Products',
      icon: 'pi-box',
      route: '/vendor/products',
      roles: ['VENDOR'],
    },
    {
      label: 'Add New Products',
      icon: 'pi-plus',
      route: '/vendor/product/create',
      roles: ['VENDOR'],
    },

    // Customer
    {
      label: 'Home',
      icon: 'pi-home',
      route: '/customer/dashboard',
      roles: ['CUSTOMER'],
    },
    {
      label: 'My Cart',
      icon: 'pi-shopping-cart',
      route: '/cart',
      roles: ['CUSTOMER'],
    },

    // Shared
    {
      label: 'Products',
      icon: 'pi-box',
      route: '/products',
      roles: ['ADMIN', 'CUSTOMER'],
    },
    {
      label: 'Categories',
      icon: 'pi-th-large',
      route: '/categories',
      roles: ['ADMIN'],
    },
    {
      label: 'Orders',
      icon: 'pi-list-check',
      route: '/vendor/orders',
      roles: ['ADMIN', 'VENDOR'],
    },
    {
      label: 'Orders',
      icon: 'pi-list-check',
      route: '/orders',
      roles: ['CUSTOMER'],
    },
    {
      label: 'My Profile',
      icon: 'pi-building',
      route: '/profile',
      roles: ['VENDOR', 'CUSTOMER'],
    },
    {
      label: 'Switch Vendor',
      icon: 'pi-users',
      route: '/vendor/selection',
      roles: ['VENDOR'],
    },
  ];

  constructor(
    private auth: AuthService,
    private router: Router,
    private vendorContextService: VendorContextService,
  ) {}

  ngOnInit(): void {
    this.role = this.auth.getRole();
    this.id = this.auth.getUserId();
    if (this.role == 'VENDOR') {
      this.vendorId = this.vendorContextService.getVendorId();

      console.log('Vendor ID:', this.vendorId);
    }
  }

  get filteredNav(): NavItem[] {
    return this.navItems.filter(
      (item) => this.role && item.roles.includes(this.role),
    );
  }

  closeCallback(): void {
    this.sidebarVisible = true;
  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
