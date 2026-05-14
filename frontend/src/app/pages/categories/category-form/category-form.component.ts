import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CategoryService } from '../../../services/category.service';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="card max-w-2xl mx-auto">
      <div class="card-header">
        <h2 class="card-title">{{ isEditMode ? 'Edit Category' : 'Create Category' }}</h2>
      </div>
      
      <div class="card-body">
        <div *ngIf="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>

        <form [formGroup]="categoryForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label class="form-label">Category Name</label>
            <input type="text" class="form-control" formControlName="name"
              [class.is-invalid]="categoryForm.get('name')?.invalid && categoryForm.get('name')?.touched">
            <div class="invalid-feedback" *ngIf="categoryForm.get('name')?.invalid && categoryForm.get('name')?.touched">
              Category name is required.
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Description (Optional)</label>
            <textarea class="form-control" formControlName="description" rows="4"></textarea>
          </div>

          <div class="form-actions">
            <a routerLink="/admin/categories" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary" [disabled]="categoryForm.invalid || isSaving">
              <span class="material-icons spin" *ngIf="isSaving">autorenew</span>
              {{ isSaving ? 'Saving...' : (isEditMode ? 'Update' : 'Create') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .max-w-2xl { max-width: 42rem; }
    .mx-auto { margin-left: auto; margin-right: auto; }
    .alert-error {
      background-color: #fee2e2;
      color: #991b1b;
      border: 1px solid #f87171;
      padding: 1rem;
      border-radius: var(--radius-md);
      margin-bottom: 1.5rem;
    }
    .form-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem; }
    .spin { animation: spin 1s linear infinite; margin-right: 0.5rem; font-size: 1.2rem; }
    @keyframes spin { 100% { transform: rotate(360deg); } }
  `]
})
export class CategoryFormComponent implements OnInit {
  categoryForm: FormGroup;
  isEditMode = false;
  categoryId!: number;
  isSaving = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.categoryForm = this.fb.group({
      name: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.categoryId = +id;
      this.loadCategory();
    }
  }

  loadCategory() {
    this.categoryService.getById(this.categoryId).subscribe({
      next: (res) => {
        if (res.data) {
          this.categoryForm.patchValue({
            name: res.data.name,
            description: res.data.description
          });
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load category details.';
      }
    });
  }

  onSubmit() {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    const request = this.isEditMode
      ? this.categoryService.update(this.categoryId, this.categoryForm.value)
      : this.categoryService.create(this.categoryForm.value);

    request.subscribe({
      next: () => {
        this.isSaving = false;
        this.router.navigate(['/admin/categories']);
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMessage = err.error?.message || `Failed to ${this.isEditMode ? 'update' : 'create'} category.`;
      }
    });
  }
}
