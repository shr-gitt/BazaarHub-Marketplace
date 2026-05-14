import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';
import { ProductResponse } from '../../../core/models/product.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ProductCardComponent } from '../../products/product-card/product-card.component';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="dashboard-header mb-4">
      <div>
        <h1 class="card-title">Welcome to BazaarHub</h1>
        <p class="text-secondary mt-1">
          Discover the best products from our verified vendors.
        </p>
      </div>
      <a routerLink="/products" class="btn btn-primary">
        Browse All Products
      </a>
    </div>

    <!-- Recommended Products Section -->
    <div class="section-container">
      <div class="section-header">
        <h2 class="section-title">Recommended For You</h2>
        <a routerLink="/products" class="view-all">View All</a>
      </div>

      <app-loading-spinner *ngIf="isLoading"></app-loading-spinner>

      <app-empty-state
        *ngIf="!isLoading && recommendedProducts.length === 0"
        icon="inventory_2"
        title="No products found"
        description="Check back later for new arrivals."
      ></app-empty-state>

      <div
        class="product-grid"
        *ngIf="!isLoading && recommendedProducts.length > 0"
      >
        <app-product-card
          *ngFor="let product of recommendedProducts"
          [product]="product"
          (addToCart)="addToCart(product.id)"
        ></app-product-card>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="section-container mt-4">
      <h2 class="section-title">Quick Links</h2>
      <div class="quick-links-grid">
        <a routerLink="/orders" class="quick-card">
          <div class="icon-circle bg-blue">
            <span class="material-icons">receipt_long</span>
          </div>
          <h3>My Orders</h3>
          <p>Track, return, or buy things again</p>
        </a>
        <a routerLink="/cart" class="quick-card">
          <div class="icon-circle bg-green">
            <span class="material-icons">shopping_cart</span>
          </div>
          <h3>Shopping Cart</h3>
          <p>View items you've added for purchase</p>
        </a>
        <a routerLink="/customer/profile" class="quick-card">
          <div class="icon-circle bg-purple">
            <span class="material-icons">manage_accounts</span>
          </div>
          <h3>Account Settings</h3>
          <p>Manage your profile and addresses</p>
        </a>
      </div>
    </div>
  `,
  styles: [
    `
      .dashboard-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 1rem;
        background: var(--bg-primary);
        padding: 2rem;
        border-radius: var(--radius-lg);
        border: 1px solid var(--border-color);
      }
      .section-container {
        margin-bottom: 2rem;
      }
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
      }
      .section-title {
        font-size: 1.25rem;
        color: var(--text-primary);
        margin: 0;
      }
      .view-all {
        font-size: 0.875rem;
        font-weight: 500;
        color: var(--primary-color);
      }
      .product-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 1.5rem;
      }

      .quick-links-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 1.5rem;
      }
      .quick-card {
        background: var(--bg-primary);
        border: 1px solid var(--border-color);
        border-radius: var(--radius-lg);
        padding: 1.5rem;
        text-decoration: none;
        color: inherit;
        transition: all 0.2s;
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
      }
      .quick-card:hover {
        border-color: var(--primary-light);
        box-shadow: var(--shadow-md);
        transform: translateY(-2px);
      }
      .icon-circle {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 1rem;
        color: white;
      }
      .icon-circle .material-icons {
        font-size: 2rem;
      }
      .bg-blue {
        background-color: #3b82f6;
      }
      .bg-green {
        background-color: #10b981;
      }
      .bg-purple {
        background-color: #8b5cf6;
      }

      .quick-card h3 {
        font-size: 1.125rem;
        margin: 0 0 0.5rem 0;
      }
      .quick-card p {
        color: var(--text-secondary);
        font-size: 0.875rem;
        margin: 0;
      }
    `,
  ],
})
export class CustomerDashboardComponent implements OnInit {
  recommendedProducts: ProductResponse[] = [];
  isLoading = true;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
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
        alert('Item added to cart!');
      },
      error: (err: any) => {
        alert(err.error?.message || 'Failed to add item to cart');
      },
    });
  }
}
