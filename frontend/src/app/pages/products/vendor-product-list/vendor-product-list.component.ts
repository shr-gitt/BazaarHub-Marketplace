import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ProductResponse } from '../../../core/models/product.model';
import { CategoryResponse } from '../../../core/models/category.model';
import { ProductService } from '../../../services/product.service';
import { CategoryService } from '../../../services/category.service';
import { ActivatedRoute, Router } from '@angular/router';
import { VendorContextService } from '../../../services/vendor-context.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

@Component({
  selector: 'app-vendor-product-list',
  standalone: true,
  imports: [
    CommonModule,
    EmptyStateComponent,
    ToastModule,
    TableModule,
    ButtonModule,
    ConfirmDialogModule,
  ],
  templateUrl: './vendor-product-list.component.html',
  styleUrl: './vendor-product-list.component.scss',
  providers: [MessageService, ConfirmationService],
})
export class VendorProductListComponent implements OnInit {
  vendorId: number | null = null;
  products: ProductResponse[] = [];
  categories: CategoryResponse[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 0;
  pageSize = 10;
  totalRecords = 0;
  selectedCategoryId: number | null = null;
  allLoadedProducts: ProductResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private categoryService: CategoryService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private vendorContextService: VendorContextService,
  ) {}

  ngOnInit() {
    this.vendorId = this.vendorContextService.getVendorId();

    this.loadCategories();
    this.loadProducts(this.vendorId!, 0);
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
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
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
          this.totalRecords = res.data.totalElements;
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

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.pageSize = event.rows;
    this.loadProducts(this.vendorId!, page);
  }

  onEdit(productId: number) {
    this.router.navigate(['/vendor/product/edit', productId]);
  }

  onDelete(event: Event, productId: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget, // attach popup to the button
      message: 'Are you sure you want to delete this product?',
      header: 'Delete Product',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        this.productService.delete(productId).subscribe({
          next: () => {
            this.loadProducts(this.vendorId!, this.currentPage);
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail:
                err?.error?.message || 'Something went wrong, try again later.',
            });
          },
        });
      },
    });
  }
}
