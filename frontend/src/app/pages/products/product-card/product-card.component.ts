import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductResponse } from '../../../core/models/product.model';
import { AuthModelService } from '../../../services/auth-model.service';
import { AuthService } from '../../../services/auth.service';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, ButtonModule, ToastModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss',
})
export class ProductCardComponent {
  @Input() product!: ProductResponse;
  @Input() isVendorView: boolean = false;
  @Input() addingToCart = false;

  @Output() edit = new EventEmitter<number>();
  @Output() delete = new EventEmitter<number>();
  @Output() addToCart = new EventEmitter<number>();

  constructor(
    public authService: AuthService,
    public authModelService: AuthModelService,
  ) {}

  get imageSrc(): string {
    if (!this.product.imageUrl) {
      return 'assets/no-image.png';
    }

    return this.product.imageUrl;
  }

  onEdit(): void {
    this.edit.emit(this.product.id);
  }

  onDelete(): void {
    if (!confirm('Are you sure you want to delete this product?')) return;
    this.delete.emit(this.product.id);
  }

  onAddToCart(): void {
    this.addToCart.emit(this.product.id);
  }

  get isCustomer(): boolean {
    return this.authService.getRole() === 'CUSTOMER';
  }
}
