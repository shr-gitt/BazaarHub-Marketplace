import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import { ProductRequest, ProductResponse } from '../core/models/product.model';


@Injectable({ providedIn: 'root' })
export class ProductService {
  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10): Observable<ApiResponse<PageResponse<ProductResponse>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<ProductResponse>>>(API_URLS.GET_PRODUCTS, { params });
  }

  getById(id: number): Observable<ApiResponse<ProductResponse>> {
    return this.http.get<ApiResponse<ProductResponse>>(API_URLS.GET_PRODUCT(id));
  }

  create(dto: ProductRequest, file: File): Observable<ApiResponse<ProductResponse>> {
    const form = new FormData();
    form.append('productRequestDto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
    form.append('file', file);
    return this.http.post<ApiResponse<ProductResponse>>(API_URLS.CREATE_PRODUCT, form);
  }

  update(id: number, dto: ProductRequest): Observable<ApiResponse<ProductResponse>> {
    return this.http.post<ApiResponse<ProductResponse>>(API_URLS.UPDATE_PRODUCT(id), dto);
  }

  delete(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(API_URLS.DELETE_PRODUCT(id));
  }

  getRecommended(page = 0, size = 6): Observable<ApiResponse<PageResponse<ProductResponse>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<ProductResponse>>>(API_URLS.GET_RECOMMENDED_PRODUCTS, { params });
  }
}
