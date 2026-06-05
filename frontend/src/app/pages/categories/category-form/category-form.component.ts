import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ButtonModule],
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.scss']
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
