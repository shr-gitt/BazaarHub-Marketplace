import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import { CategoryRequest, CategoryResponse } from '../core/models/category.model';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 20): Observable<ApiResponse<PageResponse<CategoryResponse>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<CategoryResponse>>>(API_URLS.GET_CATEGORIES, { params });
  }

  getById(id: number): Observable<ApiResponse<CategoryResponse>> {
    return this.http.get<ApiResponse<CategoryResponse>>(API_URLS.GET_CATEGORY(id));
  }

  create(dto: CategoryRequest): Observable<ApiResponse<CategoryResponse>> {
    return this.http.post<ApiResponse<CategoryResponse>>(API_URLS.CREATE_CATEGORY, dto);
  }

  update(id: number, dto: CategoryRequest): Observable<ApiResponse<CategoryResponse>> {
    return this.http.post<ApiResponse<CategoryResponse>>(API_URLS.UPDATE_CATEGORY(id), dto);
  }

  delete(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(API_URLS.DELETE_CATEGORY(id));
  }
}
