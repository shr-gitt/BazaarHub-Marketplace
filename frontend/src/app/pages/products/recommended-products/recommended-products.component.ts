import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ProductService } from '../../../services/product.service';
import { ProductResponse } from '../../../core/models/product.model';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-recommended-products',
  standalone: true,
  imports: [CommonModule, RouterModule, ToastModule],
  templateUrl: './recommended-products.component.html',
  styleUrl: './recommended-products.component.scss',
  providers: [MessageService],
})
export class RecommendedProductsComponent implements OnInit {
  products: ProductResponse[] = [];

  page = 0;
  size = 6;
  totalPages = 0;

  loading = false;

  constructor(
    private productService: ProductService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadRecommendedProducts();
  }

  loadRecommendedProducts(): void {
    this.loading = true;

    this.productService.getRecommended(this.page, this.size).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.data) {
          this.products = res.data.content;
          this.totalPages = res.data.totalPages;
        }
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }

  nextPage(): void {
    if (this.page + 1 >= this.totalPages) return;

    this.page++;
    this.loadRecommendedProducts();
  }

  previousPage(): void {
    if (this.page === 0) return;

    this.page--;
    this.loadRecommendedProducts();
  }
}
