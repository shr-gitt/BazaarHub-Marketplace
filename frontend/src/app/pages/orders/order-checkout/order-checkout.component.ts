import { Component } from '@angular/core';
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

import { OrderService } from '../../../services/order.service';
import { OrderSummaryComponent } from '../order-summary/order-summary.component';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

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
    OrderSummaryComponent,
    ToastModule,
  ],
  templateUrl: './order-checkout.component.html',
  styleUrl: './order-checkout.component.scss',
  providers: [MessageService],
})
export class OrderCheckoutComponent {
  isSubmitting = false;

  checkoutForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private router: Router,
    private messageService: MessageService,
  ) {
    this.checkoutForm = this.fb.group({
      shippingAddress: ['', Validators.required],
      contactNumber: [
        '',
        [Validators.required, Validators.pattern('^[0-9]{10}$')],
      ],
      remark: [''],
    });

    this.checkoutForm.valueChanges.subscribe(() => {});
  }

  placeOrder(): void {
    this.checkoutForm.markAllAsTouched();
    if (this.checkoutForm.invalid) {
      return;
    }
    this.isSubmitting = true;

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
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
      },
    });
  }
}
