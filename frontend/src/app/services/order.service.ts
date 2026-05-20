import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import {
  OrderRequest,
  OrderResponse,
  OrderStatusUpdateRequest,
} from '../core/models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(private http: HttpClient) {}

  placeOrder(dto: OrderRequest): Observable<ApiResponse<OrderResponse>> {
    return this.http.post<ApiResponse<OrderResponse>>(API_URLS.CHECKOUT, dto);
  }

  getMyOrders(): Observable<ApiResponse<OrderResponse[]>> {
    return this.http.get<ApiResponse<OrderResponse[]>>(API_URLS.GET_ORDERS);
  }

  getById(id: number): Observable<ApiResponse<OrderResponse>> {
    return this.http.get<ApiResponse<OrderResponse>>(API_URLS.GET_ORDER(id));
  }

  updateStatus(
    id: number,
    dto: OrderStatusUpdateRequest,
  ): Observable<ApiResponse<OrderResponse>> {
    return this.http.patch<ApiResponse<OrderResponse>>(
      API_URLS.UPDATE_ORDER_STATUS(id),
      dto,
    );
  }

  cancelOrder(id: number): Observable<ApiResponse<OrderResponse>> {
    return this.http.patch<ApiResponse<OrderResponse>>(
      API_URLS.CANCEL_ORDER(id),
      {},
    );
  }
}
