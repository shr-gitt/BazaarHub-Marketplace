import {
  Component,
  OnInit,
  OnDestroy,
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

import { NotificationService } from '../../services/notification.service';
import { NotificationWebSocketService } from '../../services/notification-websocket.service';

import { NotificationResponse } from '../../core/models/notification.model';

import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [
    CommonModule,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
})
export class NotificationsComponent
  implements OnInit, OnDestroy
{
  notifications: NotificationResponse[] = [];
  isLoading = true;

  private destroy$ = new Subject<void>();

  constructor(
    private notificationService: NotificationService,
    private websocketService: NotificationWebSocketService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    this.websocketService.disconnect();
  }

  private connectWebSocket(): void {
    const token = localStorage.getItem('token');

    if (!token) {
      console.warn('No JWT token found');
      return;
    }

    this.websocketService.connect(token);

    this.websocketService.notifications$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (notification) => {
          this.notifications.unshift(notification);
        },
        error: (err) => {
          console.error(
            'Notification websocket error',
            err
          );
        },
      });
  }

  loadNotifications(): void {
    this.isLoading = true;

    this.notificationService.getAll().subscribe({
      next: (res) => {
        this.notifications = res.data ?? [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      },
    });
  }

  markAsRead(notification: NotificationResponse): void {
    if (notification.isRead) {
      return;
    }

    this.notificationService
      .markAsRead(notification.id)
      .subscribe({
        next: () => {
          notification.isRead = true;
        },
      });
  }

  markAllRead(): void {
    this.notificationService
      .markAllAsRead()
      .subscribe({
        next: () => {
          this.notifications.forEach(
            (n) => (n.isRead = true)
          );
        },
      });
  }

  getIcon(type: string): string {
    if (type.includes('ORDER')) return 'shopping_bag';
    if (type.includes('PAYMENT')) return 'payments';
    if (type.includes('VENDOR')) return 'storefront';

    return 'notifications';
  }

  getIconClass(type: string): string {
    if (type.includes('ORDER')) return 'icon-order';
    if (type.includes('PAYMENT')) return 'icon-payment';

    if (
      type.includes('REJECTED') ||
      type.includes('FAILED')
    ) {
      return 'icon-alert';
    }

    return 'icon-system';
  }
}