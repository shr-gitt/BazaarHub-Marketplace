import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { CartService } from '../../../services/cart.service';
import { CartResponse } from '../../../core/models/cart.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  template: `
    <div class="checkout-container">
      <h1 class="page-title mb-4">Checkout</h1>

      <app-loading-spinner *ngIf="isLoading"></app-loading-spinner>
      
      <div *ngIf="errorMessage" class="alert alert-error mb-4">
        {{ errorMessage }}
      </div>

      <div class="checkout-grid" *ngIf="!isLoading && cart && cart.items.length > 0">
        <div class="checkout-form-section">
          <div class="card mb-4">
            <div class="card-header">
              <h2 class="card-title text-xl">Shipping Details</h2>
            </div>
            <div class="card-body">
              <form [formGroup]="checkoutForm" (ngSubmit)="placeOrder()">
                <div class="form-group">
                  <label class="form-label">Full Shipping Address</label>
                  <textarea class="form-control" formControlName="shippingAddress" rows="3" placeholder="Enter your full delivery address..."
                    [class.is-invalid]="checkoutForm.get('shippingAddress')?.invalid && checkoutForm.get('shippingAddress')?.touched"></textarea>
                  <div class="invalid-feedback" *ngIf="checkoutForm.get('shippingAddress')?.invalid && checkoutForm.get('shippingAddress')?.touched">
                    Shipping address is required.
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Contact Number</label>
                  <input type="text" class="form-control" formControlName="contactNumber" placeholder="e.g. 9800000000"
                    [class.is-invalid]="checkoutForm.get('contactNumber')?.invalid && checkoutForm.get('contactNumber')?.touched">
                  <div class="invalid-feedback" *ngIf="checkoutForm.get('contactNumber')?.invalid && checkoutForm.get('contactNumber')?.touched">
                    Please enter a valid 10-digit phone number.
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Order Remarks / Special Instructions (Optional)</label>
                  <textarea class="form-control" formControlName="remark" rows="2"></textarea>
                </div>

                <div class="payment-section mt-4 pt-4 border-top">
                  <h3 class="font-bold mb-3">Payment Method</h3>
                  <div class="payment-option selected">
                    <span class="material-icons text-primary mr-2">local_shipping</span>
                    <span>Cash on Delivery (COD)</span>
                    <span class="material-icons ml-auto text-primary">check_circle</span>
                  </div>
                  <p class="text-sm text-secondary mt-2">Currently, we only support Cash on Delivery. You will pay when the order is delivered to you.</p>
                </div>
              </form>
            </div>
          </div>
        </div>
        
        <div class="checkout-summary-section">
          <div class="card summary-card">
            <div class="card-header">
              <h3 class="card-title text-lg">Order Summary</h3>
            </div>
            <div class="card-body p-0">
              <div class="summary-items">
                <div class="summary-item" *ngFor="let item of cart.items">
                  <div class="item-qty">{{ item.quantity }}x</div>
                  <div class="item-name">{{ item.productName }}</div>
                  <div class="item-price">NPR {{ item.totalPrice }}</div>
                </div>
              </div>
            </div>
            <div class="card-body bg-secondary border-top">
              <div class="summary-row">
                <span class="text-secondary">Subtotal</span>
                <span class="font-medium">NPR {{ cart.totalPrice }}</span>
              </div>
              <div class="summary-row">
                <span class="text-secondary">Shipping</span>
                <span class="font-medium">Free</span>
              </div>
              
              <div class="summary-total mt-3 pt-3 border-top">
                <span class="text-lg font-bold">Total</span>
                <span class="text-xl font-bold text-primary">NPR {{ cart.totalPrice }}</span>
              </div>
              
              <button 
                class="btn btn-primary btn-lg w-100 mt-4 flex justify-center align-center" 
                (click)="placeOrder()" 
                [disabled]="checkoutForm.invalid || isSubmitting">
                <span class="material-icons spin mr-2" *ngIf="isSubmitting">autorenew</span>
                {{ isSubmitting ? 'Processing...' : 'Place Order' }}
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div *ngIf="!isLoading && (!cart || cart.items.length === 0)" class="text-center mt-5">
        <h2>Your cart is empty</h2>
        <p>You cannot checkout with an empty cart.</p>
      </div>
    </div>
  `,
  styles: [`
    .page-title { font-size: 2rem; margin: 0 0 1.5rem 0; color: var(--text-primary); }
    .mb-3 { margin-bottom: 1rem; }
    .mb-4 { margin-bottom: 1.5rem; }
    .mt-2 { margin-top: 0.5rem; }
    .mt-3 { margin-top: 1rem; }
    .mt-4 { margin-top: 1.5rem; }
    .mt-5 { margin-top: 3rem; }
    .pt-3 { padding-top: 1rem; }
    .pt-4 { padding-top: 1.5rem; }
    .p-0 { padding: 0 !important; }
    .border-top { border-top: 1px solid var(--border-color); }
    .bg-secondary { background-color: var(--bg-secondary); }
    
    .checkout-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 2rem;
    }
    @media (min-width: 992px) {
      .checkout-grid { grid-template-columns: 3fr 2fr; }
      .summary-card { position: sticky; top: 90px; }
    }
    
    .payment-option {
      display: flex;
      align-items: center;
      padding: 1rem;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      background-color: var(--bg-primary);
    }
    .payment-option.selected {
      border-color: var(--primary-color);
      background-color: var(--primary-light);
    }
    
    .summary-items { max-height: 300px; overflow-y: auto; padding: 1.5rem; }
    .summary-item { display: flex; align-items: flex-start; margin-bottom: 1rem; gap: 1rem; }
    .summary-item:last-child { margin-bottom: 0; }
    .item-qty { font-weight: 600; color: var(--text-secondary); width: 30px; }
    .item-name { flex: 1; font-weight: 500; }
    .item-price { font-weight: 600; white-space: nowrap; }
    
    .summary-row { display: flex; justify-content: space-between; margin-bottom: 0.5rem; }
    .summary-total { display: flex; justify-content: space-between; align-items: center; }
    
    .flex { display: flex; }
    .align-center { align-items: center; }
    .justify-center { justify-content: center; }
    .w-100 { width: 100%; }
    .text-sm { font-size: 0.875rem; }
    .text-lg { font-size: 1.125rem; }
    .text-xl { font-size: 1.5rem; }
    .font-medium { font-weight: 500; }
    .font-bold { font-weight: 700; }
    .text-secondary { color: var(--text-secondary); }
    .text-primary { color: var(--primary-color); }
    .mr-2 { margin-right: 0.5rem; }
    .ml-auto { margin-left: auto; }
    .btn-lg { padding: 1rem 1.5rem; font-size: 1.125rem; font-weight: 600; border-radius: var(--radius-md); }
    .spin { animation: spin 1s linear infinite; }
    .alert-error { background-color: #fee2e2; color: #991b1b; border: 1px solid #f87171; padding: 1rem; border-radius: var(--radius-md); }
    .text-center { text-align: center; }
  `]
})
export class CheckoutComponent implements OnInit {
  cart: CartResponse | null = null;
  isLoading = true;
  isSubmitting = false;
  errorMessage = '';
  checkoutForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {
    this.checkoutForm = this.fb.group({
      shippingAddress: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      remark: ['']
    });
  }

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.cartService.getCart().subscribe({
      next: (res) => {
        this.cart = res.data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  placeOrder() {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      return;
    }

    if (!this.cart || this.cart.items.length === 0) return;

    this.isSubmitting = true;
    this.errorMessage = '';

    this.orderService.placeOrder(this.checkoutForm.value).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        // After successful checkout, clear cart state locally or rely on backend doing it
        // Navigate to orders page
        alert('Order placed successfully!');
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Failed to place order. Please try again.';
      }
    });
  }
}
