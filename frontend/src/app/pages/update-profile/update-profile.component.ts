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
import { TabViewModule } from 'primeng/tabview';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { MessageModule } from 'primeng/message';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

type Tab = 'account' | 'profile';

@Component({
  selector: 'app-update-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    VendorProfileComponent,
    CustomerProfileSetupComponent,
    TabViewModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    SelectModule,
    MessageModule,
    ProgressSpinnerModule,
    DividerModule,
    ToastModule,
  ],
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.scss'],
  providers: [MessageService],
})
export class UpdateProfileComponent implements OnInit {
  activeTab: Tab = 'account';
  userId: number | null = null;
  role: string | null = null;

  loadingAccount = false;
  savingAccount = false;

  accountForm!: FormGroup;
  genderOptions = [
    { label: 'Male', value: 'MALE' },
    { label: 'Female', value: 'FEMALE' },
    { label: 'Other', value: 'OTHER' },
  ];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private messageService: MessageService,
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
      error: (err) => {
        this.loadingAccount = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }

  saveAccount(): void {
    this.accountForm.markAllAsTouched();

    if (this.accountForm.invalid) {
      return;
    }

    if (!this.userId) {
      return;
    }

    this.savingAccount = true;

    const payload = { ...this.accountForm.value };

    if (!payload.password) {
      delete payload.password;
    }

    this.userService.update(this.userId, payload).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Account details updated.',
        });
        this.savingAccount = false;

        this.accountForm.patchValue({
          password: '',
        });
      },

      error: (err: any) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
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
