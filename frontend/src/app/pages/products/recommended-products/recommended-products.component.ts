import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ProductService } from '../../../services/product.service';
import { ProductResponse } from '../../../core/models/product.model';

@Component({
  selector: 'app-recommended-products',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './recommended-products.component.html',
  styleUrl: './recommended-products.component.scss',
})
export class RecommendedProductsComponent implements OnInit {
  products: ProductResponse[] = [];

  page = 0;
  size = 6;
  totalPages = 0;

  loading = false;
  error = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadRecommendedProducts();
  }

  loadRecommendedProducts(): void {
    this.loading = true;
    this.error = '';

    this.productService.getRecommended(this.page, this.size).subscribe({
      next: (res) => {
        this.loading = false;
        this.products = res.data.content;
        this.totalPages = res.data.totalPages;
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err.error?.message || 'Failed to load recommended products.';
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
