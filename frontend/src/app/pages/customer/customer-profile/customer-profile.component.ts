import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { UserResponse } from '../../../core/models/user.model';
import { Gender } from '../../../core/models/auth.model';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-customer-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  template: './customer-profile.component.html',
  styles: ['./customer-profile.component.scss']
})
export class CustomerProfileComponent implements OnInit {
  profileForm: FormGroup;
  currentUser: UserResponse | null = null;
  isLoading = true;
  isSaving = false;
  successMessage = '';
  errorMessage = '';

  genders = Object.values(Gender);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      gender: ['', Validators.required],
      password: ['']
    });
  }

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    const userId = this.authService.getUserId();
    if (!userId) return;

    this.isLoading = true;
    this.userService.getById(userId).subscribe({
      next: (res) => {
        if (res.data) {
          this.currentUser = res.data;
          this.profileForm.patchValue({
            firstName: res.data.firstName,
            lastName: res.data.lastName,
            phoneNumber: res.data.phoneNumber,
            gender: res.data.gender
          });
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load profile data.';
        this.isLoading = false;
      }
    });
  }

  onSubmit() {
    if (this.profileForm.invalid || !this.currentUser) return;

    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';

    const updateData = {
      ...this.profileForm.value,
      email: this.currentUser.email,
      role: this.currentUser.role,
      password: this.profileForm.value.password || 'dummyPassword' // Need to check how backend handles empty password on update
    };

    this.userService.update(this.currentUser.id, updateData).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.successMessage = 'Profile updated successfully!';
        this.profileForm.markAsPristine();
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMessage = err.error?.message || 'Failed to update profile.';
      }
    });
  }
}
