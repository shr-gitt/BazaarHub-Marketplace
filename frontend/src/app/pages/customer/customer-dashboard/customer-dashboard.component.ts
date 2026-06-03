import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';
import { ProductResponse } from '../../../core/models/product.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ProductCardComponent } from '../../products/product-card/product-card.component';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ToastModule,
    ButtonModule
  ],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.scss'],
  providers: [MessageService],
})
export class CustomerDashboardComponent implements OnInit {
  recommendedProducts: ProductResponse[] = [];
  isLoading = true;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private messageService: MessageService,
  ) {}

  ngOnInit() {
    this.fetchRecommendedProducts();
  }

  fetchRecommendedProducts() {
    this.isLoading = true;
    this.productService.getRecommended(0, 8).subscribe({
      next: (res: any) => {
        if (res.data) {
          this.recommendedProducts = res.data.content || res.data;
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  addToCart(productId: number) {
    this.cartService.addItem({ productId, quantity: 1 }).subscribe({
      next: (res: any) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Product added to cart.',
        });
      },
      error: (err: any) => {
        alert(err.error?.message || 'Failed to add item to cart');
      },
    });
  }
}
