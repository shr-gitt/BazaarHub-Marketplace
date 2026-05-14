import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent, EmptyStateComponent],
  template: `
    <div class="card">
      <div class="card-header flex justify-between align-center">
        <h2 class="card-title">Category Management</h2>
        <a routerLink="/admin/categories/create" class="btn btn-primary">
          <span class="material-icons mr-2">add</span> Add Category
        </a>
      </div>
      
      <app-loading-spinner *ngIf="isLoading"></app-loading-spinner>
      
      <app-empty-state 
        *ngIf="!isLoading && categories.length === 0"
        icon="category"
        title="No categories found"
        description="There are currently no categories. Create one to get started."
      ></app-empty-state>

      <div class="card-body p-0" *ngIf="!isLoading && categories.length > 0">
        <div class="table-container no-border-radius">
          <table class="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Description</th>
                <th>Status</th>
                <th class="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let category of categories">
                <td>#{{ category.id }}</td>
                <td class="font-medium">{{ category.name }}</td>
                <td>{{ category.description || 'No description' }}</td>
                <td>
                  <span class="status-badge" 
                        [ngClass]="{'success': category.status === 'ACTIVE', 'error': category.status === 'INACTIVE'}">
                    {{ category.status }}
                  </span>
                </td>
                <td class="text-right">
                  <div class="action-buttons">
                    <button class="icon-btn text-warning" [routerLink]="['/admin/categories/edit', category.id]" title="Edit">
                      <span class="material-icons">edit</span>
                    </button>
                    <button class="icon-btn text-error" (click)="deleteCategory(category.id)" title="Delete">
                      <span class="material-icons">delete</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="pagination" *ngIf="totalPages > 1">
          <button class="pagination-btn" [disabled]="currentPage === 0" (click)="loadCategories(currentPage - 1)">
            <span class="material-icons">chevron_left</span>
          </button>
          <span class="pagination-info">Page {{ currentPage + 1 }} of {{ totalPages }}</span>
          <button class="pagination-btn" [disabled]="currentPage >= totalPages - 1" (click)="loadCategories(currentPage + 1)">
            <span class="material-icons">chevron_right</span>
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .flex { display: flex; }
    .justify-between { justify-content: space-between; }
    .align-center { align-items: center; }
    .mr-2 { margin-right: 0.5rem; }
    .p-0 { padding: 0 !important; }
    .no-border-radius { border-radius: 0; border: none; }
    .font-medium { font-weight: 500; }
    .text-warning { color: var(--warning-color); }
    .text-error { color: var(--error-color); }
    .action-buttons { display: flex; gap: 0.5rem; justify-content: flex-end; }
    .icon-btn { background: none; border: none; cursor: pointer; padding: 0.25rem; display: flex; align-items: center; justify-content: center; border-radius: var(--radius-sm); }
    .icon-btn:hover { background-color: var(--bg-secondary); }
    .icon-btn .material-icons { font-size: 1.25rem; }
  `]
})
export class CategoryListComponent implements OnInit {
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;

  constructor(private categoryService: CategoryService) {}

  ngOnInit() {
    this.loadCategories(0);
  }

  loadCategories(page: number) {
    this.isLoading = true;
    this.categoryService.getAll(page, 20).subscribe({
      next: (res) => {
        if (res.data) {
          this.categories = res.data.content;
          this.currentPage = res.data.number;
          this.totalPages = res.data.totalPages;
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  deleteCategory(id: number) {
    if (confirm('Are you sure you want to delete this category?')) {
      this.categoryService.delete(id).subscribe({
        next: () => {
          this.loadCategories(this.currentPage);
        },
        error: (err) => {
          alert(err.error?.message || 'Failed to delete category.');
        }
      });
    }
  }
}
