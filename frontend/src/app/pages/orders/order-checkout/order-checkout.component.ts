import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { FloatLabelModule } from 'primeng/floatlabel';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

import { OrderService } from '../../../services/order.service';
import { OrderSummaryComponent } from '../order-summary/order-summary.component';

@Component({
  selector: 'app-order-checkout',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    ButtonModule,
    InputTextModule,
    TextareaModule,
    FloatLabelModule,
    ProgressSpinnerModule,
    OrderSummaryComponent,
  ],
  templateUrl: './order-checkout.component.html',
  styleUrl: './order-checkout.component.scss',
})
export class OrderCheckoutComponent {
  isSubmitting = false;
  errorMessage = '';

  checkoutForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private router: Router,
  ) {
    this.checkoutForm = this.fb.group({
      shippingAddress: ['', Validators.required],
      contactNumber: [
        '',
        [Validators.required, Validators.pattern('^[0-9]{10}$')],
      ],
      remark: [''],
    });
  }

  placeOrder(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      return;
    }
    this.isSubmitting = true;
    this.errorMessage = '';

    this.orderService.placeOrder(this.checkoutForm.value).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        this.router.navigate(['/payment'], {
          state: {
            orderId: res.data.id,
          },
        });
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage =
          err.error?.message || 'Failed to place order. Please try again.';
      },
    });
  }
}
