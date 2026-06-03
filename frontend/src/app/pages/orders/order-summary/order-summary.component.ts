import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../services/cart.service';
import { CartResponse } from '../../../core/models/cart.model';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-order-summary',
  standalone: true,
  imports: [CommonModule, CardModule, ToastModule],
  templateUrl: './order-summary.component.html',
  styleUrl: './order-summary.component.scss',
  providers: [MessageService],
})
export class OrderSummaryComponent implements OnInit {
  cart: CartResponse | null = null;
  isLoading = false;

  constructor(
    private cartService: CartService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  get subtotal(): number {
    return this.cart?.totalPrice ?? 0;
  }

  loadCart(): void {
    this.isLoading = true;
    this.cartService.getCart().subscribe({
      next: (res) => {
        this.cart = res.data;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }
}
