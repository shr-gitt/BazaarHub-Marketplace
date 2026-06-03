import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ButtonModule,
  ],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss'],
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
      },
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
        },
      });
    }
  }
}
