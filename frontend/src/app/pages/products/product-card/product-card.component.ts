import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductResponse } from '../../../core/models/product.model';
import { AuthModelService } from '../../../services/auth-model.service';
import { AuthService } from '../../../services/auth.service';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, ButtonModule, ToastModule, ConfirmDialogModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss',
  providers: [ConfirmationService],
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
    private confirmationService: ConfirmationService,
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

  onDelete(event: Event): void {
    this.confirmationService.confirm({
      target: event.target as EventTarget, 
      message: 'Are you sure you want to delete this product?',
      header: 'Delete Product',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        this.delete.emit(this.product.id);
      },
    });
  }

  onAddToCart(): void {
    this.addToCart.emit(this.product.id);
  }

  get isCustomer(): boolean {
    return this.authService.getRole() === 'CUSTOMER';
  }
}
