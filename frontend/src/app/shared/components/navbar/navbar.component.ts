import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
//   username: string | null = null;
userId: number | null = null;
  role: string | null = null;
  unreadCount = 0;

  constructor(
    private auth: AuthService,
    private notifService: NotificationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.userId = this.auth.getUserId();
    this.role = this.auth.getRole();
    this.loadUnreadCount();
  }

  loadUnreadCount(): void {
    this.notifService.getUnreadCount().subscribe({
      next: (res) => (this.unreadCount = res.data || 0),
      error: () => {}, // silent fail
    });
  }

  get dashboardRoute(): string {
    if (this.role === 'ADMIN') return '/admin/dashboard';
    if (this.role === 'VENDOR') return '/vendor/dashboard';
    return '/customer/dashboard';
  }
}