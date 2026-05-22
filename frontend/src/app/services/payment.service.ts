import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import {
  PaymentRequest,
  PaymentResponse,
  CashPaymentConfirmRequest,
} from '../core/models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  constructor(private http: HttpClient) {}

  // POST /api/payment/create
  createPayment(dto: PaymentRequest): Observable<ApiResponse<PaymentResponse>> {
    return this.http.post<ApiResponse<PaymentResponse>>(
      API_URLS.CREATE_PAYMENT,
      dto,
    );
  }

  // POST /api/payment/cash/confirm
  confirmCashPayment(
    dto: CashPaymentConfirmRequest,
  ): Observable<ApiResponse<PaymentResponse>> {
    return this.http.post<ApiResponse<PaymentResponse>>(
      API_URLS.CONFIRM_CASH_PAYMENT,
      dto,
    );
  }

  // GET /api/payment/:id
  getPaymentById(id: number): Observable<ApiResponse<PaymentResponse>> {
    return this.http.get<ApiResponse<PaymentResponse>>(
      API_URLS.GET_PAYMENT(id),
    );
  }

  // GET /api/payments
  getAllPayments(
    page: number = 0,
    size: number = 10,
  ): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(API_URLS.GET_PAYMENTS, {
      params: { page, size, sort: 'modifiedAt,desc' },
    });
  }
}
