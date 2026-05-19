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
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss'],
})
export class ProductListComponent implements OnInit {
  products: ProductResponse[] = [];
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;
  selectedCategoryId: number | null = null;

  allLoadedProducts: ProductResponse[] = [];

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
