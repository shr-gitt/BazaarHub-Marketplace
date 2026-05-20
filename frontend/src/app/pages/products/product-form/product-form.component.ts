import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CategoryResponse } from '../../../core/models/category.model';
import { ProductService } from '../../../services/product.service';
import { CategoryService } from '../../../services/category.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss'],
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
    private route: ActivatedRoute,
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      discountPrice: [''],
      stockQuantity: ['', [Validators.required, Validators.min(0)]],
      categoryId: ['', Validators.required],
      vendorId: [null],
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
      this.productForm.patchValue({
        vendorId: Number(localStorage.getItem('userId')),
      });
    }
  }

  loadCategories() {
    this.categoryService.getAll(0, 100).subscribe({
      next: (res) => {
        if (res.data) {
          this.categories = res.data.content;
        }
      },
    });
  }

  loadProduct() {
    this.productService.getById(this.productId).subscribe({
      next: (res) => {
        if (res.data) {
          const cat = this.categories.find(
            (c) => c.name === res.data!.category,
          );

          this.productForm.patchValue({
            name: res.data.name,
            description: res.data.description,
            price: res.data.price,
            discountPrice: res.data.discountPrice,
            stockQuantity: res.data.stockQuantity,
            categoryId: cat ? cat.id : '',
            vendorId: Number(localStorage.getItem('userId')),
          });

          if (res.data.imageUrl) {
            this.imagePreview = res.data.imageUrl;
          }
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load product details.';
      },
    });
  }

  onFileSelect(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList && fileList.length > 0) {
      this.selectedFile = fileList[0];

      // Image preview
      const reader = new FileReader();
      reader.onload = (e) => (this.imagePreview = reader.result);
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
      this.productService
        .update(this.productId, this.productForm.value)
        .subscribe({
          next: () => {
            this.isSaving = false;
            this.router.navigate(['/vendor/products']);
          },
          error: (err) => {
            this.isSaving = false;
            this.errorMessage =
              err.error?.message || 'Failed to update product.';
          },
        });
    } else {
      this.productService
        .create(this.productForm.value, this.selectedFile!)
        .subscribe({
          next: () => {
            this.isSaving = false;
            this.router.navigate(['/vendor/products']);
          },
          error: (err) => {
            this.isSaving = false;
            this.errorMessage =
              err.error?.message || 'Failed to create product.';
          },
        });
    }
  }
}
