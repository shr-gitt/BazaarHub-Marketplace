import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { FloatLabelModule } from 'primeng/floatlabel';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { RadioButtonModule } from 'primeng/radiobutton';

import { OrderSummaryComponent } from '../../orders/order-summary/order-summary.component';
import {
  PaymentRequest,
  PaymentType,
} from '../../../core/models/payment.model';
import { PaymentService } from '../../../services/payment.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-order-payment',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    OrderSummaryComponent,
    CardModule,
    ButtonModule,
    TextareaModule,
    InputTextModule,
    FloatLabelModule,
    RadioButtonModule,
    ProgressSpinnerModule,
  ],
  templateUrl: './create-order-payment.component.html',
  styleUrl: './create-order-payment.component.scss',
})
export class OrderPaymentComponent {
  orderId: number;
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';

  paymentForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private paymentService: PaymentService,
  ) {
    const navigation = this.router.getCurrentNavigation();
    this.orderId = Number(navigation?.extras?.state?.['orderId']) ?? null;

    if (!this.orderId) {
      this.router.navigate(['/cart']);
      return;
    }

    this.paymentForm = this.fb.group({
      paymentType: ['', Validators.required],
    });
  }

  proceedToPayment(): void {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }
    this.isSubmitting = true;
    this.errorMessage = '';

    const payload: PaymentRequest = {
      orderId: this.orderId,
      paymentType: this.paymentForm.value.paymentType,
    };

    this.paymentService.createPayment(payload).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        if (this.paymentForm.value.paymentType == 'WALLET')
          this.submitEsewaForm(res.data);
        else this.router.navigate(['/orders']);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage =
          err.error?.message || 'Failed to perform payment. Please try again.';
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
