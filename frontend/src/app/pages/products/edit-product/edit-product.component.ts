import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ProductService } from '../../../services/product.service';
import {
  ProductRequest,
  ProductResponse,
} from '../../../core/models/product.model';
import { VendorContextService } from '../../../services/vendor-context.service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-edit-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ToastModule, ButtonModule],
  templateUrl: './edit-product.component.html',
  styleUrl: './edit-product.component.scss',
  providers: [MessageService],
})
export class EditProductComponent implements OnInit {
  productForm: FormGroup;

  productId!: number;
  loading = false;
  saving = false;

  existingImageUrl: string | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private vendorContextService: VendorContextService,
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      price: [null, [Validators.required, Validators.min(1)]],
      discountPrice: [null],
      stockQuantity: [null, [Validators.required, Validators.min(1)]],
      categoryId: [null, Validators.required],
    });
  }

  ngOnInit(): void {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));

    if (!this.productId) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Invalid product ID.',
      });
      return;
    }

    this.loadProduct();
  }

  loadProduct(): void {
    this.loading = true;

    this.productService.getById(this.productId).subscribe({
      next: (res) => {
        this.loading = false;

        const product: ProductResponse | undefined = res.data;

        if (!product) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Product not found.',
          });
          return;
        }

        this.existingImageUrl = product.imageUrl || null;

        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          discountPrice: product.discountPrice || null,
          stockQuantity: product.stockQuantity,
          categoryId: product.categoryId,
        });
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err.error?.message || 'Failed to load product.',
        });
      },
    });
  }

  onSubmit(): void {
    this.productForm.markAllAsTouched();

    if (this.productForm.invalid) {
      return;
    }

    const vendorId = this.vendorContextService.getVendorId();

    if (!vendorId) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Vendor not found. Please login again.',
      });
      return;
    }

    this.saving = true;

    const dto: ProductRequest = {
      name: this.productForm.value.name,
      description: this.productForm.value.description,
      price: Number(this.productForm.value.price),
      discountPrice: this.productForm.value.discountPrice
        ? Number(this.productForm.value.discountPrice)
        : null,
      stockQuantity: Number(this.productForm.value.stockQuantity),
      categoryId: Number(this.productForm.value.categoryId),
      vendorId: vendorId,
    };

    this.productService.update(this.productId, dto).subscribe({
      next: () => {
        this.saving = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Product updated successfully.',
        });

        setTimeout(() => {
          this.router.navigate(['/vendor/products']);
        }, 1000);
      },
      error: (err) => {
        this.saving = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || 'Failed to update product.',
        });
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/vendor/products']);
  }
}
