import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { ProductRequest } from '../../../core/models/product.model';
import { VendorContextService } from '../../../services/vendor-context.service';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CategoryResponse } from '../../../core/models/category.model';
import { CategoryService } from '../../../services/category.service';
import { DropdownModule } from 'primeng/dropdown';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

@Component({
  selector: 'app-create-product',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ToastModule,
    ButtonModule,
    DropdownModule,
    ConfirmDialogModule,
  ],
  templateUrl: './create-product.component.html',
  styleUrl: './create-product.component.scss',
  providers: [MessageService, ConfirmationService],
})
export class CreateProductComponent implements OnInit {
  productForm: FormGroup;
  loading = false;
  imageError = false;
  categories: CategoryResponse[] = [];
  vendorId: number | null = null;

  selectedImage?: File;
  imagePreview: string | ArrayBuffer | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private vendorContextService: VendorContextService,
    private router: Router,
    private messageService: MessageService,
    private categoryService: CategoryService,
    private confirmationService: ConfirmationService,
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      price: [null, [Validators.required, Validators.min(1)]],
      discountPrice: [null, Validators.min(1)],
      stockQuantity: [null, [Validators.required, Validators.min(1)]],
      categoryId: [null, Validators.required],
    });
  }

  ngOnInit(): void {
    this.vendorId = this.vendorContextService.getVendorId();
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAll(0, 100).subscribe({
      next: (res) => {
        this.categories = res.data?.content || [];
      },
      error: (err) => {
        console.error('Failed to load categories:', err);
      },
    });
  }

  onImageSelected(event: Event): void {
    this.imageError = false;
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) return;

    this.selectedImage = input.files[0];

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(this.selectedImage);
  }

  onSubmit(event: Event): void {
    this.productForm.markAllAsTouched();

    if (this.productForm.invalid) return;

    if (!this.selectedImage) {
      this.imageError = true;
      return;
    }

    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure you want to create this product?',
      header: 'Create Product',
      icon: 'pi pi-question-circle',
      acceptLabel: 'Yes, Create',
      rejectLabel: 'Cancel',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        this.createProduct();
      },
    });
  }

  private createProduct(): void {
    this.loading = true;

    const dto: ProductRequest = {
      name: this.productForm.value.name,
      description: this.productForm.value.description,
      price: Number(this.productForm.value.price),
      discountPrice: this.productForm.value.discountPrice
        ? Number(this.productForm.value.discountPrice)
        : null,
      stockQuantity: Number(this.productForm.value.stockQuantity),
      categoryId: Number(this.productForm.value.categoryId),
      vendorId: this.vendorId!,
    };

    this.productService.create(dto, this.selectedImage!).subscribe({
      next: () => {
        this.loading = false;

        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Product created successfully.',
        });

        setTimeout(() => {
          this.router.navigate(['/vendor/products', this.vendorId]);
        }, 1000);
      },
      error: (err) => {
        this.loading = false;

        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err?.error?.message || 'Failed to create product.',
        });
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/vendor/products']);
  }
}
