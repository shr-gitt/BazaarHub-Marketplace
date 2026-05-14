import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';
import { NotificationResponse } from '../../core/models/notification.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, EmptyStateComponent],
  template: `
    <div class="notifications-container max-w-4xl mx-auto">
      <div class="flex justify-between align-center mb-4">
        <h1 class="page-title m-0">Notifications</h1>
        <button class="btn btn-secondary" (click)="markAllRead()" *ngIf="notifications.length > 0">
          Mark all as read
        </button>
      </div>

      <app-loading-spinner *ngIf="isLoading"></app-loading-spinner>
      
      <app-empty-state 
        *ngIf="!isLoading && notifications.length === 0"
        icon="notifications_none"
        title="All caught up!"
        description="You have no new notifications."
      ></app-empty-state>

      <div class="notification-list" *ngIf="!isLoading && notifications.length > 0">
        <div class="notification-card card" 
             *ngFor="let notification of notifications" 
             [class.unread]="!notification.isRead"
             (click)="markAsRead(notification)">
          
          <div class="notification-icon" [ngClass]="getIconClass(notification.type)">
            <span class="material-icons">{{ getIcon(notification.type) }}</span>
          </div>
          
          <div class="notification-content">
            <h4 class="notification-title m-0">{{ notification.title }}</h4>
            <p class="notification-message m-0 text-secondary">{{ notification.message }}</p>
            <div class="notification-time text-sm text-tertiary mt-1">
              {{ notification.createdAt | date:'medium' }}
            </div>
          </div>
          
          <div class="unread-dot" *ngIf="!notification.isRead"></div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .max-w-4xl { max-width: 56rem; width: 100%; margin-left: auto; margin-right: auto; }
    .page-title { font-size: 2rem; color: var(--text-primary); }
    .m-0 { margin: 0; }
    .mt-1 { margin-top: 0.25rem; }
    .mb-4 { margin-bottom: 1.5rem; }
    .flex { display: flex; }
    .align-center { align-items: center; }
    .justify-between { justify-content: space-between; }
    .text-sm { font-size: 0.875rem; }
    .text-secondary { color: var(--text-secondary); }
    .text-tertiary { color: var(--text-tertiary); }
    
    .notification-list { display: flex; flex-direction: column; gap: 1rem; }
    
    .notification-card {
      display: flex;
      align-items: flex-start;
      padding: 1.25rem;
      gap: 1rem;
      cursor: pointer;
      transition: all 0.2s;
      border-left: 4px solid transparent;
    }
    .notification-card:hover { background-color: var(--bg-secondary); }
    .notification-card.unread { background-color: #f8fafc; border-left-color: var(--primary-color); }
    
    .notification-icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }
    .notification-icon .material-icons { font-size: 1.5rem; color: white; }
    
    .icon-order { background-color: #3b82f6; }
    .icon-payment { background-color: #10b981; }
    .icon-system { background-color: #8b5cf6; }
    .icon-alert { background-color: #ef4444; }
    
    .notification-content { flex: 1; }
    .notification-title { font-size: 1rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.25rem !important; }
    
    .unread-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background-color: var(--primary-color);
      margin-top: 0.5rem;
      flex-shrink: 0;
    }
  `]
})
export class NotificationsComponent implements OnInit {
  notifications: NotificationResponse[] = [];
  isLoading = true;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.isLoading = true;
    this.notificationService.getAll().subscribe({
      next: (res: any) => {
        if (res.data) {
          this.notifications = res.data;
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  markAsRead(notification: NotificationResponse) {
    if (!notification.isRead) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          notification.isRead = true;
        }
      });
    }
  }

  markAllRead() {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
      }
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
    if (type.includes('REJECTED') || type.includes('FAILED')) return 'icon-alert';
    return 'icon-system';
  }
}
