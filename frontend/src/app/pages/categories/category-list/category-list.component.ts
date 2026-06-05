import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ButtonModule,
    ConfirmDialogModule,
  ],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss'],
  providers: [ConfirmationService],
})
export class CategoryListComponent implements OnInit {
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;

  constructor(
    private categoryService: CategoryService,
    private confirmationService: ConfirmationService,
  ) {}

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
      },
    });
  }

  confirmDelete(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      header: 'Delete Category',
      message: 'Are you sure you want to delete this category?',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',

      accept: () => {
        this.categoryService.delete(id).subscribe({
          next: () => {
            this.loadCategories(this.currentPage);
          },
          error: (err) => {
            alert(err.error?.message || 'Failed to delete category.');
          },
        });
      },
    });
  }
}
