import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCardComponent } from '../product-card/product-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ProductResponse } from '../../../core/models/product.model';
import { CategoryResponse } from '../../../core/models/category.model';
import { ProductService } from '../../../services/product.service';
import { CategoryService } from '../../../services/category.service';
import { CartService } from '../../../services/cart.service';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../../services/auth.service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ToastModule,
    CardModule,
    TagModule,
    ButtonModule,
  ],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss'],
  providers: [MessageService],
})
export class ProductListComponent implements OnInit {
  products: ProductResponse[] = [];
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;
  selectedCategoryId: number | null = null;
  allLoadedProducts: ProductResponse[] = [];
  role: string | null = null;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService,
    private authService: AuthService,
    private messageService: MessageService,
  ) {}

  ngOnInit() {
    this.role = this.authService.getRole();
    this.loadCategories();
    this.loadProducts(0);
  }
  get isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  loadCategories() {
    this.categoryService.getAll(0, 100).subscribe({
      next: (res) => {
        if (res.data) {
          this.categories = res.data.content.filter(
            (c) => c.status === 'ACTIVE',
          );
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load categories.',
        });
      },
    });
  }

  loadProducts(page: number) {
    this.isLoading = true;
    this.productService.getAll(page, 20).subscribe({
      next: (res) => {
        if (res.data) {
          this.allLoadedProducts = res.data.content;
          this.currentPage = res.data.number;
          this.totalPages = res.data.totalPages;
          this.applyCategoryFilter();
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load products. Please try again.',
        });
      },
    });
  }

  filterByCategory(categoryId: number | null) {
    this.selectedCategoryId = categoryId;
    this.applyCategoryFilter();
  }

  applyCategoryFilter() {
    if (this.selectedCategoryId === null) {
      this.products = this.allLoadedProducts;
    } else {
      const category = this.categories.find(
        (c) => c.id === this.selectedCategoryId,
      );
      if (category) {
        this.products = this.allLoadedProducts.filter(
          (p) => p.category === category.name,
        );
      } else {
        this.products = [];
      }
    }
  }

  onAddToCart(productId: number) {
    this.cartService.addItem({ productId, quantity: 1 }).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Product added to cart successfully.',
        });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || 'Failed to add product to cart.',
        });
      },
    });
  }
}
