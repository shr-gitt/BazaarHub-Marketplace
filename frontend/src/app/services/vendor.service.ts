import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../core/models/api-response.model';
import { Vendor, VendorRequest, ApprovalRequest, VendorResponse } from '../core/models/vendor.model';

@Injectable({ providedIn: 'root' })
export class VendorService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // GET /api/vendors
  getAll(page = 0, size = 10): Observable<ApiResponse<PageResponse<Vendor>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<Vendor>>>(`${this.base}/vendors`, { params });
  }

  //GET /api/vendors/my
  getByUser(): Observable<ApiResponse<Vendor[]>> {
    return this.http.get<ApiResponse<Vendor[]>>(`${this.base}/vendors/my`);
  }

  // GET /api/vendor/{id}
  getById(id: number): Observable<ApiResponse<Vendor>> {
    return this.http.get<ApiResponse<Vendor>>(`${this.base}/vendor/${id}`);
  }

  // POST /api/vendor/create
  create(dto: VendorRequest): Observable<ApiResponse<Vendor>> {
    return this.http.post<ApiResponse<Vendor>>(`${this.base}/vendor/create`, dto);
  }

  // POST /api/vendor/update/{id}
  update(id:number, dto: VendorRequest): Observable<ApiResponse<VendorResponse>> {
    return this.http.post<ApiResponse<VendorResponse>>(`${this.base}/vendor/update/${id}`, dto);
  }

  // POST /api/vendor/approval/{vendorId}
  approve(vendorId: number, dto: ApprovalRequest): Observable<ApiResponse<Vendor>> {
    return this.http.post<ApiResponse<Vendor>>(`${this.base}/vendor/approval/${vendorId}`, dto);
  }

  // POST /api/vendor/delete/{id}
  delete(id: number): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(`${this.base}/vendor/delete/${id}`, {});
  }
}
