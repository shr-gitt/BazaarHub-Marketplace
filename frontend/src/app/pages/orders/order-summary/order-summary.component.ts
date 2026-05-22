import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../services/cart.service';
import { CartResponse } from '../../../core/models/cart.model';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-order-summary',
  standalone: true,
  imports: [CommonModule, CardModule], 
  templateUrl: './order-summary.component.html',
  styleUrl: './order-summary.component.scss',
})
export class OrderSummaryComponent implements OnInit {
  cart: CartResponse | null = null;
  isLoading = false;

  constructor(private cartService: CartService) {}

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
      error: () => {
        this.isLoading = false;
      },
    });
  }
}