import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { ProductCardComponent } from '../product-card/product-card.component';
import { ProductResponse } from '../../../core/models/product.model';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    LoadingSpinnerComponent,
    ProductCardComponent,
    ToastModule,
  ],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss'],
  providers: [MessageService],
})
export class ProductDetailComponent implements OnInit {
  product: ProductResponse | null = null;
  isLoading = true;
  quantity = 1;
  isAdding = false;

  recommendedProducts: ProductResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private messageService: MessageService,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.loadProduct(+id);
      }
    });
  }

  loadProduct(id: number) {
    this.isLoading = true;
    this.productService.getById(id).subscribe({
      next: (res) => {
        this.product = res.data;
        this.quantity = 1;
        this.isLoading = false;
        this.recommendedProducts = [];
        this.loadRecommended();
      },
      error: () => {
        this.isLoading = false;
        this.product = null;
      },
    });
  }

  loadRecommended() {
    this.productService.getRecommended(0, 4).subscribe({
      next: (res) => {
        if (res.data) {
          // Filter out current product
          this.recommendedProducts = res.data.content
            .filter((p) => p.id !== this.product?.id)
            .slice(0, 4);
        }
      },
      error: () => {
        this.recommendedProducts = [];
      },
    });
  }

  increaseQty() {
    if (this.product && this.quantity < this.product.stockQuantity) {
      this.quantity++;
    }
  }

  decreaseQty() {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  addToCart() {
    if (!this.product) return;
    this.isAdding = true;

    this.cartService
      .addItem({ productId: this.product.id, quantity: this.quantity })
      .subscribe({
        next: () => {
          this.isAdding = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Successfully added to cart!',
          });
        },
        error: (err) => {
          this.isAdding = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail:
              err?.error?.message || 'Something went wrong, try again later.',
          });
        },
      });
  }

  onRecommendedAddToCart(productId: number) {
    this.cartService.addItem({ productId, quantity: 1 }).subscribe({
      next: () =>
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Product added to cart.',
        }),
      error: (err) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        }),
    });
  }
}
