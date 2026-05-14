import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import { CartItemRequest, CartResponse, UpdateCartItemRequest } from '../core/models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  constructor(private http: HttpClient) {}

  getCart(): Observable<ApiResponse<CartResponse>> {
    return this.http.get<ApiResponse<CartResponse>>(API_URLS.CART);
  }

  addItem(dto: CartItemRequest): Observable<ApiResponse<CartResponse>> {
    return this.http.post<ApiResponse<CartResponse>>(API_URLS.CART_ITEMS, dto);
  }

  updateItem(productId: number, dto: UpdateCartItemRequest): Observable<ApiResponse<CartResponse>> {
    return this.http.post<ApiResponse<CartResponse>>(API_URLS.CART_ITEM(productId), dto);
  }

  removeItem(productId: number): Observable<ApiResponse<CartResponse>> {
    return this.http.delete<ApiResponse<CartResponse>>(API_URLS.CART_ITEM(productId));
  }

  clearCart(): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(API_URLS.CART);
  }
}
