import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartResponse, CartItemResponse } from '../../core/models/cart.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent, EmptyStateComponent],
  template: `
    <div class="cart-container">
      <h1 class="page-title mb-4">Shopping Cart</h1>

      <app-loading-spinner *ngIf="isLoading"></app-loading-spinner>

      <app-empty-state
        *ngIf="!isLoading && (!cart || cart.items.length === 0)"
        icon="shopping_cart"
        title="Your cart is empty"
        description="Looks like you haven't added anything to your cart yet."
        actionLabel="Start Shopping"
        (action)="navigateHome()"
      ></app-empty-state>

      <div class="cart-grid" *ngIf="!isLoading && cart && cart.items.length > 0">
        <div class="cart-items-section">
          <div class="card mb-4" *ngFor="let item of cart.items">
            <div class="card-body p-4 flex align-center justify-between item-row">
              <div class="item-details flex align-center gap-3">
                <div class="item-image-placeholder">
                  <span class="material-icons text-secondary">image</span>
                </div>
                <div>
                  <h3 class="item-name m-0">
                    <a [routerLink]="['/product', item.productId]">{{ item.productName }}</a>
                  </h3>
                  <div class="item-status text-sm mt-1"
                       [ngClass]="{'text-error': item.status !== 'AVAILABLE', 'text-success': item.status === 'AVAILABLE'}">
                    {{ item.status }}
                  </div>
                </div>
              </div>

              <div class="item-actions flex align-center gap-4">
                <div class="item-price text-right hide-mobile">
                  <div class="text-secondary text-sm">Price</div>
                  <div class="font-medium">NPR {{ item.pricePerUnit }}</div>
                </div>

                <div class="qty-controls">
                  <button (click)="updateQuantity(item.productId, item.quantity - 1)" [disabled]="item.quantity <= 1 || isUpdating">-</button>
                  <span class="qty-value">{{ item.quantity }}</span>
                  <button (click)="updateQuantity(item.productId, item.quantity + 1)" [disabled]="isUpdating">+</button>
                </div>

                <div class="item-total text-right">
                  <div class="text-secondary text-sm">Total</div>
                  <div class="font-bold">NPR {{ item.totalPrice }}</div>
                </div>

                <button class="icon-btn text-error remove-btn" (click)="removeItem(item.productId)" [disabled]="isUpdating" title="Remove">
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </div>
          </div>

          <div class="flex justify-between align-center mt-4 pt-4 border-top">
            <button class="btn btn-secondary text-error" (click)="clearCart()" [disabled]="isUpdating">
              <span class="material-icons mr-1" style="font-size: 1.1rem;">delete_sweep</span> Clear Cart
            </button>
            <a routerLink="/products" class="btn btn-secondary">Continue Shopping</a>
          </div>
        </div>

        <div class="cart-summary-section">
          <div class="card summary-card">
            <div class="card-header">
              <h3 class="card-title text-lg">Order Summary</h3>
            </div>
            <div class="card-body">
              <div class="summary-row">
                <span>Subtotal ({{ cart.items.length }} items)</span>
                <span class="font-medium">NPR {{ cart.totalPrice }}</span>
              </div>
              <div class="summary-row">
                <span>Shipping</span>
                <span class="text-secondary">Calculated at checkout</span>
              </div>

              <div class="summary-total mt-4 pt-3 border-top">
                <span class="text-lg font-bold">Total</span>
                <span class="text-xl font-bold text-primary">NPR {{ cart.totalPrice }}</span>
              </div>

              <a routerLink="/checkout" class="btn btn-primary btn-lg w-100 mt-4 flex justify-center align-center">
                Proceed to Checkout <span class="material-icons ml-2">arrow_forward</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-title { font-size: 2rem; margin: 0 0 1.5rem 0; color: var(--text-primary); }
    .mb-4 { margin-bottom: 1.5rem; }
    .mt-4 { margin-top: 1.5rem; }
    .mt-1 { margin-top: 0.25rem; }
    .pt-3 { padding-top: 1rem; }
    .pt-4 { padding-top: 1.5rem; }
    .p-4 { padding: 1.5rem; }
    .m-0 { margin: 0; }
    .gap-3 { gap: 1rem; }
    .gap-4 { gap: 1.5rem; }
    .flex { display: flex; }
    .align-center { align-items: center; }
    .justify-between { justify-content: space-between; }
    .justify-center { justify-content: center; }
    .text-right { text-align: right; }
    .w-100 { width: 100%; }
    .text-sm { font-size: 0.875rem; }
    .text-lg { font-size: 1.125rem; }
    .text-xl { font-size: 1.5rem; }
    .font-medium { font-weight: 500; }
    .font-bold { font-weight: 700; }
    .text-secondary { color: var(--text-secondary); }
    .text-primary { color: var(--primary-color); }
    .text-error { color: var(--error-color); }
    .text-success { color: var(--success-color); }
    .border-top { border-top: 1px solid var(--border-color); }
    .mr-1 { margin-right: 0.25rem; }
    .ml-2 { margin-left: 0.5rem; }

    .cart-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 2rem;
    }
    @media (min-width: 992px) {
      .cart-grid { grid-template-columns: 2fr 1fr; }
      .summary-card { position: sticky; top: 90px; }
    }

    .item-image-placeholder { width: 80px; height: 80px; background-color: var(--bg-secondary); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; border: 1px solid var(--border-color); }
    .item-name a { color: var(--text-primary); text-decoration: none; font-size: 1.125rem; transition: color 0.2s; }
    .item-name a:hover { color: var(--primary-color); }

    .qty-controls { display: flex; align-items: center; border: 1px solid var(--border-color); border-radius: var(--radius-md); background: var(--bg-primary); }
    .qty-controls button { background: none; border: none; width: 32px; height: 32px; font-size: 1.25rem; display: flex; align-items: center; justify-content: center; cursor: pointer; color: var(--text-secondary); }
    .qty-controls button:hover:not(:disabled) { background: var(--bg-secondary); color: var(--text-primary); }
    .qty-controls button:disabled { opacity: 0.5; cursor: not-allowed; }
    .qty-value { padding: 0 0.75rem; font-weight: 600; font-size: 1rem; min-width: 30px; text-align: center; }

    .icon-btn { background: none; border: none; cursor: pointer; padding: 0.5rem; display: flex; align-items: center; justify-content: center; border-radius: var(--radius-sm); }
    .icon-btn:hover:not(:disabled) { background-color: #fee2e2; }
    .icon-btn:disabled { opacity: 0.5; cursor: not-allowed; }

    .summary-row { display: flex; justify-content: space-between; margin-bottom: 0.75rem; }
    .summary-total { display: flex; justify-content: space-between; align-items: center; }

    .btn-lg { padding: 1rem 1.5rem; font-size: 1.125rem; font-weight: 600; border-radius: var(--radius-md); }

    @media (max-width: 768px) {
      .item-row { flex-direction: column; align-items: flex-start; gap: 1rem; }
      .item-actions { width: 100%; justify-content: space-between; }
      .hide-mobile { display: none; }
    }
  `]
})
export class CartComponent implements OnInit {
  cart: CartResponse | null = null;
  isLoading = true;
  isUpdating = false;

  constructor(private cartService: CartService) {}

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.isLoading = true;
    this.cartService.getCart().subscribe({
      next: (res: any) => {
        this.cart = res.data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  updateQuantity(productId: number, quantity: number) {
    if (quantity < 1) return;
    this.isUpdating = true;
    this.cartService.updateItem(productId, { quantity }).subscribe({
      next: (res: any) => {
        this.cart = res.data;
        this.isUpdating = false;
      },
      error: (err: any) => {
        this.isUpdating = false;
        alert(err.error?.message || 'Failed to update quantity');
      }
    });
  }

  removeItem(productId: number) {
    this.isUpdating = true;
    this.cartService.removeItem(productId).subscribe({
      next: (res: any) => {
        this.cart = res.data;
        this.isUpdating = false;
      },
      error: (err: any) => {
        this.isUpdating = false;
        alert(err.error?.message || 'Failed to remove item');
      }
    });
  }

  clearCart() {
    if (confirm('Are you sure you want to remove all items from your cart?')) {
      this.isUpdating = true;
      this.cartService.clearCart().subscribe({
        next: () => {
          if (this.cart) {
            this.cart.items = [];
            this.cart.totalPrice = 0;
          }
          this.isUpdating = false;
        },
        error: (err: any) => {
          this.isUpdating = false;
          alert(err.error?.message || 'Failed to clear cart');
        }
      });
    }
  }

  navigateHome() {
    window.location.href = '/products';
  }
}
