import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCardComponent } from '../product-card/product-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ProductResponse } from '../../../core/models/product.model';
import { CategoryResponse } from '../../../core/models/category.model';
import { ProductService } from '../../../services/product.service';
import { CategoryService } from '../../../services/category.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-vendor-product-list',
  standalone: true,
  imports: [
    CommonModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './vendor-product-list.component.html',
  styleUrl: './vendor-product-list.component.scss',
})
export class VendorProductListComponent implements OnInit {
  vendorId!: number;
  products: ProductResponse[] = [];
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;
  selectedCategoryId: number | null = null;
  errorMessage = '';
  allLoadedProducts: ProductResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private categoryService: CategoryService,
  ) {}

  ngOnInit() {
    this.vendorId = Number(this.route.snapshot.paramMap.get('id'));

    this.loadCategories();
    this.loadProducts(this.vendorId, 0);
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
        this.errorMessage = 'Failed to load categories.'; 
      },
    });
  }

  loadProducts(vendorId: number, page: number) {
    this.isLoading = true;
    this.productService.getByVendorId(vendorId, page, 10).subscribe({
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

  onEdit(productId: number) {
    this.router.navigate(['/vendor/product/edit', productId]);
  }

  onDelete(productId: number) {
    this.productService.delete(productId).subscribe({
      next: () => {
        this.loadProducts(this.vendorId, this.currentPage);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete product.';
      },
    });
  }
}
