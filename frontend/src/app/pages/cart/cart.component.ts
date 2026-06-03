import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartResponse } from '../../core/models/cart.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    ButtonModule,
    ToastModule,
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
  providers: [MessageService],
})
export class CartComponent implements OnInit {
  cart: CartResponse | null = null;
  isLoading = true;
  isUpdating = false;
  isSubmitting = false;
  showClearConfirm = false;

  constructor(
    private cartService: CartService,
    private router: Router,
    private messageService: MessageService,
  ) {}

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
      },
    });
  }

  get imageSrc(): string {
    const items = this.cart?.items;
    if (!items || items.length === 0) {
      return 'assets/no-image.png';
    }

    // Use first item's image as fallback
    return items[0].productImage || 'assets/no-image.png';
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
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
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
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }

  clearCart() {
    this.showClearConfirm = false;
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
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }

  goToCheckout() {
    this.isSubmitting = true;
    this.router
      .navigate(['/checkout'])
      .then(() => {
        this.isSubmitting = false;
      })
      .catch(() => {
        this.isSubmitting = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Something went wrong, try again later.',
        });
      });
  }

  navigateHome() {
    this.router.navigate(['/products']);
  }

  get hasUnavailableItems(): boolean {
    return (
      this.cart?.items?.some((item) => item.status !== 'AVAILABLE') ?? false
    );
  }
}
