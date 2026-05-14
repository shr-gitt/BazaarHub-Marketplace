import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductResponse } from '../../../core/models/product.model';
@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss',
})
export class ProductCardComponent {
  @Input() product!: ProductResponse;
  @Input() isVendorView: boolean = false;

  @Output() edit = new EventEmitter<number>();
  @Output() delete = new EventEmitter<number>();
  @Output() addToCart = new EventEmitter<number>();

  get imageSrc(): string {
    if (!this.product.imageUrl) {
      return 'assets/no-image.png';
    }

    if (this.product.imageUrl.startsWith('http')) {
      return this.product.imageUrl;
    }

    return `http://localhost:9000/bazaarhub/${this.product.imageUrl}`;
  }

  onEdit(): void {
    this.edit.emit(this.product.id);
  }

  onDelete(): void {
    this.delete.emit(this.product.id);
  }
  onAddToCart(): void {
    this.addToCart.emit(this.product.id);
  }
}
