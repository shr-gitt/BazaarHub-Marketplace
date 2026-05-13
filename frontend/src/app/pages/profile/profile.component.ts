import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { VendorProfileComponent } from '../vendor/vendor-profile/vendor-profile.component';
import { CustomerProfileSetupComponent } from '../customer/customer-profile-setup/customer-profile-setup.component';
import { AuthService } from '../../services/auth.service';

type Tab = 'account' | 'profile';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    VendorProfileComponent,
    CustomerProfileSetupComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  activeTab: Tab = 'account';
  userId: number | null = null;
  role: string | null = null;

  loadingAccount = false;
  savingAccount = false;
  accountError = '';
  accountSuccess = '';

  accountForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getUserId();
    this.role = this.authService.getRole();

    this.accountForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', Validators.required],
      gender: ['MALE', Validators.required],
      password: [''],
    });

    this.loadAccount();
  }

  loadAccount(): void {
    if (!this.userId) {
      return;
    }

    this.loadingAccount = true;
    if (!this.userId) return;

    this.userService.getById(this.userId).subscribe({
      next: (res) => {
        if (res.data) {
          this.accountForm.patchValue({
            firstName: res.data.firstName,
            lastName: res.data.lastName,
            email: res.data.email,
            phoneNumber: res.data.phoneNumber,
            gender: res.data.gender,
          });
        }
        this.loadingAccount = false;
      },
      error: () => {
        this.loadingAccount = false;
      },
    });
  }

  saveAccount(): void {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }

    if (!this.userId) {
      return;
    }

    this.savingAccount = true;
    this.accountError = '';
    this.accountSuccess = '';

    const payload = { ...this.accountForm.value };

    // If password is empty, don't send it
    if (!payload.password) {
      delete payload.password;
    }

    this.userService.update(this.userId, payload).subscribe({
      next: () => {
        this.accountSuccess = 'Account details updated.';
        this.savingAccount = false;

        this.accountForm.patchValue({
          password: '',
        });
      },

      error: (err: any) => {
        this.accountError = err?.error?.message || 'Failed to update account.';

        this.savingAccount = false;
      },
    });
  }

  get af() {
    return this.accountForm.controls;
  }
  setTab(tab: Tab): void {
    this.activeTab = tab;
  }
}
