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

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="products-page">
      <!-- Sidebar Filters -->
      <div class="filters-sidebar">
        <div class="card mb-4">
          <div class="card-header">
            <h3 class="card-title text-lg">Categories</h3>
          </div>
          <div class="card-body p-0">
            <div class="category-list">
              <button
                class="category-btn"
                [class.active]="!selectedCategoryId"
                (click)="filterByCategory(null)"
              >
                All Categories
              </button>
              <button
                class="category-btn"
                *ngFor="let cat of categories"
                [class.active]="selectedCategoryId === cat.id"
                (click)="filterByCategory(cat.id)"
              >
                {{ cat.name }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Products Grid -->
      <div class="products-content">
        <div class="flex justify-between align-center mb-4">
          <h2 class="text-2xl font-bold m-0">All Products</h2>
        </div>

        <app-loading-spinner *ngIf="isLoading"></app-loading-spinner>

        <app-empty-state
          *ngIf="!isLoading && products.length === 0"
          icon="search_off"
          title="No products found"
          description="Try selecting a different category or check back later."
        ></app-empty-state>

        <div class="product-grid" *ngIf="!isLoading && products.length > 0">
          <app-product-card
            *ngFor="let product of products"
            [product]="product"
            (addToCart)="onAddToCart($event)"
          ></app-product-card>
        </div>

        <div class="pagination mt-4" *ngIf="totalPages > 1 && !isLoading">
          <button
            class="pagination-btn"
            [disabled]="currentPage === 0"
            (click)="loadProducts(currentPage - 1)"
          >
            <span class="material-icons">chevron_left</span>
          </button>
          <span class="pagination-info"
            >Page {{ currentPage + 1 }} of {{ totalPages }}</span
          >
          <button
            class="pagination-btn"
            [disabled]="currentPage >= totalPages - 1"
            (click)="loadProducts(currentPage + 1)"
          >
            <span class="material-icons">chevron_right</span>
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .products-page {
        display: flex;
        flex-direction: column;
        gap: 2rem;
      }
      @media (min-width: 768px) {
        .products-page {
          flex-direction: row;
          align-items: flex-start;
        }
        .filters-sidebar {
          width: 250px;
          flex-shrink: 0;
          position: sticky;
          top: 90px;
        }
        .products-content {
          flex: 1;
        }
      }
      .text-lg {
        font-size: 1.125rem;
      }
      .text-2xl {
        font-size: 1.5rem;
      }
      .font-bold {
        font-weight: 700;
      }
      .m-0 {
        margin: 0;
      }
      .mb-4 {
        margin-bottom: 1.5rem;
      }
      .mt-4 {
        margin-top: 1.5rem;
      }
      .p-0 {
        padding: 0 !important;
      }
      .flex {
        display: flex;
      }
      .justify-between {
        justify-content: space-between;
      }
      .align-center {
        align-items: center;
      }

      .category-list {
        display: flex;
        flex-direction: column;
      }
      .category-btn {
        background: none;
        border: none;
        border-bottom: 1px solid var(--border-color);
        padding: 1rem 1.5rem;
        text-align: left;
        font-size: 0.875rem;
        color: var(--text-secondary);
        cursor: pointer;
        transition: all 0.2s;
      }
      .category-btn:last-child {
        border-bottom: none;
      }
      .category-btn:hover {
        background-color: var(--bg-secondary);
        color: var(--primary-color);
      }
      .category-btn.active {
        font-weight: 600;
        color: var(--primary-color);
        background-color: var(--primary-light);
        border-left: 4px solid var(--primary-color);
      }

      .product-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 1.5rem;
      }
    `,
  ],
})
export class ProductListComponent implements OnInit {
  products: ProductResponse[] = [];
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;
  selectedCategoryId: number | null = null;

  allLoadedProducts: ProductResponse[] = []; // Store all fetched if backend pagination doesn't support category filtering directly easily

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService,
  ) {}

  ngOnInit() {
    this.loadCategories();
    this.loadProducts(0);
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
      // Find category name to filter by since product response has category name string
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
        alert('Product added to cart!');
      },
      error: (err) => {
        alert(err.error?.message || 'Failed to add product to cart.');
      },
    });
  }
}
