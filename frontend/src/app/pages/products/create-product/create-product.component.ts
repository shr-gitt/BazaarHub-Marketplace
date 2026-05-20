import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { ProductRequest } from '../../../core/models/product.model';
import { VendorService } from '../../../services/vendor.service';

@Component({
  selector: 'app-create-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-product.component.html',
  styleUrl: './create-product.component.scss',
})
export class CreateProductComponent {
  productForm: FormGroup;
  loading = false;
  error = '';
  success = '';

  vendorId: number | null = null;

  selectedImage?: File;
  imagePreview: string | ArrayBuffer | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private vendorService: VendorService,
    private router: Router,
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(1)]],
      discountPrice: [null, Validators.min(1)],
      stockQuantity: [null, [Validators.required, Validators.min(1)]],
      categoryId: [null, Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadMyVendor();
  }

  private loadMyVendor(): void {
    this.vendorService.getMyVendors().subscribe({
      next: (res) => {
        const vendor = res.data?.[0];

        if (!vendor) {
          this.error =
            'Vendor profile not found. Please create vendor profile first.';
          return;
        }

        this.vendorId = vendor.id;
      },
      error: () => {
        this.error = 'Failed to load vendor profile.';
      },
    });
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) return;

    this.selectedImage = input.files[0];

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(this.selectedImage);
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    if (!this.selectedImage) {
      this.error = 'Product image is required.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

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

    this.productService.create(dto, this.selectedImage).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Product created successfully!';

        setTimeout(() => {
          this.router.navigate(['/vendor/products']);
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Failed to create product';
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/vendor/products']);
  }
}
