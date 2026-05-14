import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../../../services/product.service';
import { CategoryService } from '../../../../services/category.service';
import { CategoryResponse } from '../../../../core/models/category.model';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="card max-w-3xl mx-auto">
      <div class="card-header">
        <h2 class="card-title">{{ isEditMode ? 'Edit Product' : 'Add New Product' }}</h2>
      </div>
      
      <div class="card-body">
        <div *ngIf="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>

        <form [formGroup]="productForm" (ngSubmit)="onSubmit()">
          
          <div class="form-group">
            <label class="form-label">Product Name</label>
            <input type="text" class="form-control" formControlName="name"
              [class.is-invalid]="productForm.get('name')?.invalid && productForm.get('name')?.touched">
            <div class="invalid-feedback" *ngIf="productForm.get('name')?.hasError('required') && productForm.get('name')?.touched">
              Product name is required.
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Price (NPR)</label>
              <input type="number" class="form-control" formControlName="price" min="0" step="0.01"
                [class.is-invalid]="productForm.get('price')?.invalid && productForm.get('price')?.touched">
              <div class="invalid-feedback" *ngIf="productForm.get('price')?.hasError('min') && productForm.get('price')?.touched">
                Price cannot be negative.
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Discount Price (Optional)</label>
              <input type="number" class="form-control" formControlName="discountPrice" min="0" step="0.01">
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Category</label>
              <select class="form-control" formControlName="categoryId"
                [class.is-invalid]="productForm.get('categoryId')?.invalid && productForm.get('categoryId')?.touched">
                <option value="" disabled>Select Category</option>
                <option *ngFor="let cat of categories" [value]="cat.id">{{ cat.name }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Stock Quantity</label>
              <input type="number" class="form-control" formControlName="stockQuantity" min="0"
                [class.is-invalid]="productForm.get('stockQuantity')?.invalid && productForm.get('stockQuantity')?.touched">
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Product Image</label>
            <!-- In edit mode, if not changing image, it's optional -->
            <input type="file" class="form-control" (change)="onFileSelect($event)" accept="image/jpeg, image/png, image/webp"
              [class.is-invalid]="!selectedFile && !isEditMode && productForm.touched">
            <div class="text-sm text-secondary mt-1" *ngIf="isEditMode">Leave blank to keep existing image.</div>
            <div class="invalid-feedback" *ngIf="!selectedFile && !isEditMode && productForm.touched">
              Image is required for new products.
            </div>
            
            <div class="mt-3" *ngIf="imagePreview">
              <img [src]="imagePreview" alt="Preview" style="max-height: 200px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Description</label>
            <textarea class="form-control" formControlName="description" rows="5"
              [class.is-invalid]="productForm.get('description')?.invalid && productForm.get('description')?.touched"></textarea>
          </div>

          <div class="form-actions mt-4">
            <a routerLink="/vendor/products" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary" [disabled]="productForm.invalid || (!selectedFile && !isEditMode) || isSaving">
              <span class="material-icons spin" *ngIf="isSaving">autorenew</span>
              {{ isSaving ? 'Saving...' : (isEditMode ? 'Update Product' : 'Create Product') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .max-w-3xl { max-width: 48rem; }
    .mx-auto { margin-left: auto; margin-right: auto; }
    .alert-error { background-color: #fee2e2; color: #991b1b; border: 1px solid #f87171; padding: 1rem; border-radius: var(--radius-md); margin-bottom: 1.5rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 1rem; }
    .spin { animation: spin 1s linear infinite; margin-right: 0.5rem; font-size: 1.2rem; }
    .text-sm { font-size: 0.875rem; }
    .text-secondary { color: var(--text-secondary); }
    .mt-1 { margin-top: 0.25rem; }
    .mt-3 { margin-top: 1rem; }
    .mt-4 { margin-top: 1.5rem; }
    @keyframes spin { 100% { transform: rotate(360deg); } }
  `]
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;
  isEditMode = false;
  productId!: number;
  isSaving = false;
  errorMessage = '';
  categories: CategoryResponse[] = [];
  
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      discountPrice: [''],
      stockQuantity: ['', [Validators.required, Validators.min(0)]],
      categoryId: ['', Validators.required],
      vendorId: [null] // Should ideally be fetched/set, but we'll see if backend handles it or we need to pass it
    });
  }

  ngOnInit() {
    this.loadCategories();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.productId = +id;
      this.loadProduct();
    } else {
      // For create, we need vendorId. 
      // Assumption: vendorId can be fetched from auth context or profile context.
      // Let's assume the backend automatically infers it from JWT for /api/create-product, 
      // but DTO has vendorId. Let's just set it to user ID for now or leave it null if backend handles.
      // We will set vendorId to localStorage userId.
      this.productForm.patchValue({ vendorId: Number(localStorage.getItem('userId')) });
    }
  }

  loadCategories() {
    this.categoryService.getAll(0, 100).subscribe({
      next: (res) => {
        if (res.data) {
          this.categories = res.data.content;
        }
      }
    });
  }

  loadProduct() {
    this.productService.getById(this.productId).subscribe({
      next: (res) => {
        if (res.data) {
          // Find category id from category string returned in response?
          // ProductResponse has `category` string, not `categoryId`.
          // We need to match it to categories list.
          const cat = this.categories.find(c => c.name === res.data!.category);
          
          this.productForm.patchValue({
            name: res.data.name,
            description: res.data.description,
            price: res.data.price,
            discountPrice: res.data.discountPrice,
            stockQuantity: res.data.stockQuantity,
            categoryId: cat ? cat.id : '',
            vendorId: Number(localStorage.getItem('userId')) // Assuming edit also needs it
          });
          
          if (res.data.imageUrl) {
            this.imagePreview = res.data.imageUrl;
          }
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load product details.';
      }
    });
  }

  onFileSelect(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList && fileList.length > 0) {
      this.selectedFile = fileList[0];
      
      // Image preview
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result;
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit() {
    if (this.productForm.invalid || (!this.selectedFile && !this.isEditMode)) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    if (this.isEditMode) {
      // Backend /api/update-product/{id} expects ProductRequestDto only, not multipart!
      // This is what the controller says: @PostMapping("/update-product/{id}") public ResponseEntity<ApiResponseDto<ProductResponseDto>> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto productRequestDto)
      this.productService.update(this.productId, this.productForm.value).subscribe({
        next: () => {
          this.isSaving = false;
          this.router.navigate(['/vendor/products']);
        },
        error: (err) => {
          this.isSaving = false;
          this.errorMessage = err.error?.message || 'Failed to update product.';
        }
      });
    } else {
      // Create product expects multipart
      this.productService.create(this.productForm.value, this.selectedFile!).subscribe({
        next: () => {
          this.isSaving = false;
          this.router.navigate(['/vendor/products']);
        },
        error: (err) => {
          this.isSaving = false;
          this.errorMessage = err.error?.message || 'Failed to create product.';
        }
      });
    }
  }
}
