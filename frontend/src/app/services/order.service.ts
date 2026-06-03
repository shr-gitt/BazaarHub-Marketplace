import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import {
  OrderItemResponse,
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
  getAdminOrders(
    page = 0,
    size = 20,
  ): Observable<ApiResponse<PageResponse<OrderResponse>>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get<ApiResponse<PageResponse<OrderResponse>>>(
      API_URLS.GET_ADMIN_ORDERS,
      { params },
    );
  }
  getVendorOrders(
    vendorId: number | null,
    page = 0,
    size = 20,
  ): Observable<ApiResponse<PageResponse<OrderItemResponse>>> {
    const params = new HttpParams()
      .set('vendorId', vendorId!)
      .set('page', page)
      .set('size', size);

    return this.http.get<ApiResponse<PageResponse<OrderItemResponse>>>(
      API_URLS.GET_VENDOR_ORDERS(vendorId!),
      { params },
    );
  }
}
