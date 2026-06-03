import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { RadioButtonModule } from 'primeng/radiobutton';

import { OrderSummaryComponent } from '../../orders/order-summary/order-summary.component';
import { PaymentRequest } from '../../../core/models/payment.model';
import { PaymentService } from '../../../services/payment.service';
import { Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-order-payment',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    OrderSummaryComponent,
    CardModule,
    ButtonModule,
    RadioButtonModule,
    ProgressSpinnerModule,
    ToastModule,
  ],
  templateUrl: './create-order-payment.component.html',
  styleUrl: './create-order-payment.component.scss',
  providers: [MessageService],
})
export class OrderPaymentComponent {
  orderId: number;
  isLoading = false;
  isSubmitting = false;

  paymentForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private paymentService: PaymentService,
    private messageService: MessageService,
  ) {
    const navigation = this.router.getCurrentNavigation();
    const id = navigation?.extras?.state?.['orderId'];
    this.orderId = id ? Number(id) : 0;

    if (!this.orderId) {
      this.router.navigate(['/cart']);
      return;
    }

    this.paymentForm = this.fb.group({
      paymentType: ['', Validators.required],
    });
  }

  proceedToPayment(): void {
    this.paymentForm.markAllAsTouched();

    if (this.paymentForm.invalid) {
      return;
    }
    this.isSubmitting = true;

    const payload: PaymentRequest = {
      orderId: this.orderId,
      paymentType: this.paymentForm.value.paymentType,
    };

    this.paymentService.createPayment(payload).subscribe({
      next: (res) => {
        if (this.paymentForm.value.paymentType === 'WALLET')
          this.submitEsewaForm(res.data);
        else this.router.navigate(['/orders']);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail:
            err?.error?.message || 'Something went wrong, try again later.',
        });
        this.isSubmitting = false;
      },
    });
  }

  submitEsewaForm(payment: any): void {
    const toFixed = (val: any) => Number(val).toFixed(2);

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = 'https://rc-epay.esewa.com.np/api/epay/main/v2/form';

    const fields = {
      amount: toFixed(payment.amount),
      tax_amount: payment.taxAmount,
      total_amount: toFixed(payment.totalAmount),
      transaction_uuid: payment.transaction_uuid,
      product_code: payment.productCode,
      product_service_charge: payment.serviceCharge,
      product_delivery_charge: payment.deliveryCharge,
      success_url: payment.successUrl,
      failure_url: payment.failureUrl,
      signed_field_names: payment.signedFieldNames,
      signature: payment.signature,
    };

    Object.entries(fields).forEach(([key, value]) => {
      const input = document.createElement('input');
      input.type = 'hidden';
      input.name = key;
      input.value = value as string;
      form.appendChild(input);
    });

    document.body.appendChild(form);
    form.submit();
  }
}
