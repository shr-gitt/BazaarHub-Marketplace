import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { ProductCardComponent } from '../product-card/product-card.component';
import { ProductResponse } from '../../../core/models/product.model';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    LoadingSpinnerComponent,
    ProductCardComponent,
  ],
  template: `
    <app-loading-spinner
      *ngIf="isLoading"
      [fullPage]="true"
    ></app-loading-spinner>

    <div *ngIf="!isLoading && product" class="product-detail-container">
      <div class="breadcrumb mb-4">
        <a routerLink="/products">Products</a> &gt;
        <span>{{ product.category }}</span> &gt;
        <span class="current">{{ product.name }}</span>
      </div>

      <div class="card mb-5">
        <div class="card-body detail-grid">
          <div class="image-section">
            <img
              [src]="product.imageUrl || 'assets/placeholder.png'"
              [alt]="product.name"
              class="main-image"
              onerror="this.src='https://placehold.co/600?text=No+Image'"
            />

            <div class="badges">
              <span class="badge sale" *ngIf="product.discountPrice">SALE</span>
              <span
                class="badge out-of-stock"
                *ngIf="product.stockQuantity <= 0"
                >OUT OF STOCK</span
              >
            </div>
          </div>

          <div class="info-section">
            <div class="category-label">{{ product.category }}</div>
            <h1 class="product-title">{{ product.name }}</h1>

            <div class="vendor-info">
              Sold by
              <span class="vendor-name">{{
                product.vendorName || 'Unknown Vendor'
              }}</span>
            </div>

            <div class="price-block my-4">
              <ng-container *ngIf="product.discountPrice; else regularPrice">
                <div class="current-price discount">
                  NPR {{ product.discountPrice | number: '1.0-2' }}
                </div>
                <div class="original-price">
                  NPR {{ product.price | number: '1.0-2' }}
                </div>
                <div class="save-badge">
                  Save NPR
                  {{ product.price - product.discountPrice | number: '1.0-2' }}
                </div>
              </ng-container>
              <ng-template #regularPrice>
                <div class="current-price">
                  NPR {{ product.price | number: '1.0-2' }}
                </div>
              </ng-template>
            </div>

            <div
              class="stock-info mb-4"
              [class.text-error]="product.stockQuantity <= 5"
            >
              <span class="material-icons">inventory_2</span>
              <span *ngIf="product.stockQuantity > 5"
                >In Stock ({{ product.stockQuantity }} available)</span
              >
              <span
                *ngIf="product.stockQuantity > 0 && product.stockQuantity <= 5"
                >Only {{ product.stockQuantity }} left in stock - order
                soon!</span
              >
              <span *ngIf="product.stockQuantity <= 0"
                >Currently Unavailable</span
              >
            </div>

            <div class="action-block mt-4 pt-4 border-top">
              <div class="quantity-selector mb-4">
                <label>Quantity</label>
                <div class="qty-controls">
                  <button (click)="decreaseQty()" [disabled]="quantity <= 1">
                    -
                  </button>
                  <input type="number" [(ngModel)]="quantity" readonly />
                  <button
                    (click)="increaseQty()"
                    [disabled]="quantity >= product.stockQuantity"
                  >
                    +
                  </button>
                </div>
              </div>

              <button
                class="btn btn-primary btn-lg w-100"
                [disabled]="product.stockQuantity <= 0 || isAdding"
                (click)="addToCart()"
              >
                <span class="material-icons mr-2">shopping_cart</span>
                {{ isAdding ? 'Adding...' : 'Add to Cart' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Product Description -->
      <div class="card mb-5">
        <div class="card-header">
          <h2 class="card-title text-xl">Product Description</h2>
        </div>
        <div class="card-body">
          <div class="description-content">
            {{ product.description }}
          </div>
        </div>
      </div>

      <!-- Recommended Products (Related) -->
      <div class="mt-5" *ngIf="recommendedProducts.length > 0">
        <h2 class="text-2xl font-bold mb-4">You might also like</h2>
        <div class="product-grid">
          <app-product-card
            *ngFor="let rec of recommendedProducts"
            [product]="rec"
            (addToCart)="onRecommendedAddToCart($event)"
          ></app-product-card>
        </div>
      </div>
    </div>

    <div
      *ngIf="!isLoading && !product"
      class="error-container text-center mt-5"
    >
      <h2>Product not found</h2>
      <p>The product you're looking for doesn't exist or has been removed.</p>
      <a routerLink="/products" class="btn btn-primary mt-3"
        >Back to Products</a
      >
    </div>
  `,
  styles: [
    `
      .breadcrumb {
        font-size: 0.875rem;
        color: var(--text-secondary);
      }
      .breadcrumb a {
        color: var(--primary-color);
        text-decoration: none;
      }
      .breadcrumb .current {
        color: var(--text-primary);
        font-weight: 500;
      }
      .mb-4 {
        margin-bottom: 1.5rem;
      }
      .mb-5 {
        margin-bottom: 3rem;
      }
      .my-4 {
        margin-top: 1.5rem;
        margin-bottom: 1.5rem;
      }
      .mt-4 {
        margin-top: 1.5rem;
      }
      .mt-5 {
        margin-top: 3rem;
      }
      .pt-4 {
        padding-top: 1.5rem;
      }
      .mr-2 {
        margin-right: 0.5rem;
      }
      .border-top {
        border-top: 1px solid var(--border-color);
      }
      .text-xl {
        font-size: 1.25rem;
      }
      .text-2xl {
        font-size: 1.5rem;
      }
      .font-bold {
        font-weight: 700;
      }
      .text-error {
        color: var(--error-color);
      }
      .text-center {
        text-align: center;
      }

      .detail-grid {
        display: grid;
        grid-template-columns: 1fr;
        gap: 2rem;
      }
      @media (min-width: 768px) {
        .detail-grid {
          grid-template-columns: 1fr 1fr;
          gap: 3rem;
        }
      }

      .image-section {
        position: relative;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        padding: 1rem;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .main-image {
        max-width: 100%;
        max-height: 500px;
        object-fit: contain;
      }

      .badges {
        position: absolute;
        top: 1rem;
        right: 1rem;
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
      }
      .badge {
        padding: 0.25rem 0.75rem;
        border-radius: var(--radius-full);
        font-size: 0.75rem;
        font-weight: 700;
        color: white;
        letter-spacing: 0.05em;
      }
      .badge.sale {
        background-color: var(--error-color);
      }
      .badge.out-of-stock {
        background-color: var(--text-tertiary);
      }

      .category-label {
        font-size: 0.875rem;
        color: var(--primary-color);
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        margin-bottom: 0.5rem;
      }
      .product-title {
        font-size: 2rem;
        margin: 0 0 0.5rem 0;
        color: var(--text-primary);
        line-height: 1.2;
      }
      .vendor-info {
        font-size: 0.875rem;
        color: var(--text-secondary);
      }
      .vendor-name {
        font-weight: 600;
        color: var(--primary-color);
      }

      .price-block {
        display: flex;
        align-items: baseline;
        flex-wrap: wrap;
        gap: 1rem;
      }
      .current-price {
        font-size: 2.5rem;
        font-weight: 700;
        color: var(--text-primary);
      }
      .current-price.discount {
        color: var(--error-color);
      }
      .original-price {
        font-size: 1.25rem;
        color: var(--text-tertiary);
        text-decoration: line-through;
      }
      .save-badge {
        background-color: #fee2e2;
        color: var(--error-color);
        padding: 0.25rem 0.75rem;
        border-radius: var(--radius-sm);
        font-size: 0.875rem;
        font-weight: 600;
      }

      .stock-info {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: var(--success-color);
        font-weight: 500;
      }

      .qty-controls {
        display: flex;
        align-items: center;
        border: 1px solid var(--border-color);
        border-radius: var(--radius-md);
        width: fit-content;
        margin-top: 0.5rem;
      }
      .qty-controls button {
        background: var(--bg-secondary);
        border: none;
        width: 40px;
        height: 40px;
        font-size: 1.25rem;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        color: var(--text-secondary);
      }
      .qty-controls button:hover:not(:disabled) {
        background: #e2e8f0;
        color: var(--text-primary);
      }
      .qty-controls button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
      .qty-controls input {
        width: 60px;
        height: 40px;
        border: none;
        border-left: 1px solid var(--border-color);
        border-right: 1px solid var(--border-color);
        text-align: center;
        font-weight: 600;
        font-size: 1rem;
        -moz-appearance: textfield;
      }

      .btn-lg {
        padding: 1rem 1.5rem;
        font-size: 1.125rem;
        font-weight: 600;
      }

      .description-content {
        line-height: 1.6;
        color: var(--text-secondary);
        white-space: pre-wrap;
      }

      .product-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 1.5rem;
      }
    `,
  ],
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
          alert('Successfully added to cart!');
        },
        error: (err) => {
          this.isAdding = false;
          alert(err.error?.message || 'Failed to add to cart.');
        },
      });
  }

  onRecommendedAddToCart(productId: number) {
    this.cartService.addItem({ productId, quantity: 1 }).subscribe({
      next: () => alert('Product added to cart!'),
      error: (err) => alert(err.error?.message || 'Failed to add product.'),
    });
  }
}
