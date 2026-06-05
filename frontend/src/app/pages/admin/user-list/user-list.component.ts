import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserResponse } from '../../../core/models/user.model';
import { UserService } from '../../../services/user.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ConfirmDialogModule,
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
  providers: [ConfirmationService],
})
export class UserListComponent implements OnInit {
  users: UserResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;

  constructor(
    private userService: UserService,
    private confirmationService: ConfirmationService,
  ) {}

  ngOnInit() {
    this.loadUsers(0);
  }

  loadUsers(page: number) {
    this.isLoading = true;
    this.userService.getAll(page, 20).subscribe({
      next: (res: any) => {
        if (res?.data) {
          this.users = res.data.content;
          this.currentPage = res.data.number;
          this.totalPages = res.data.totalPages;
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  confirmDelete(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget, // attach popup to button
      message:
        'Are you sure you want to delete this user? This action cannot be undone.',
      icon: 'pi pi-exclamation-triangle',
      header: 'Delete User?',
      acceptLabel: 'Yes, Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        this.userService.delete(id).subscribe({
          next: () => this.loadUsers(this.currentPage),
          error: (err: any) => {
            alert(err.error?.message || 'Failed to delete user.');
          },
        });
      },
    });
  }
}
