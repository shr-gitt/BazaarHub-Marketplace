import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../core/models/api-response.model';
import { CustomerProfileRequestDto, CustomerProfileResponseDto } from '../core/models/customer-profile.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  create(
    formData: FormData,
  ): Observable<ApiResponse<CustomerProfileResponseDto>> {
    return this.http.post<ApiResponse<CustomerProfileResponseDto>>(
      `${this.base}/create-customer-profile`,
      formData,
    );
  }

  getById(id: number): Observable<ApiResponse<CustomerProfileResponseDto>> {
    return this.http.get<ApiResponse<CustomerProfileResponseDto>>(
      `${this.base}/customer-profile/${id}`,
    );
  }

  getByUser(): Observable<ApiResponse<CustomerProfileResponseDto>> {
    return this.http.get<ApiResponse<CustomerProfileResponseDto>>(
      `${this.base}/customer-profile/my`,
    );
  }

  update(
    id: number,
    payload: CustomerProfileRequestDto,
  ): Observable<ApiResponse<CustomerProfileResponseDto>> {
    return this.http.post<ApiResponse<CustomerProfileResponseDto>>(
      `${this.base}/update-customer-profile/${id}`,
      payload,
    );
  }
}
