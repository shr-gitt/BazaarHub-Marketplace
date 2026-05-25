import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import { UserResponse } from '../core/models/user.model';

export interface UserResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: string;
  role: string;
  createdAt?: string;
  modifiedAt?: string;
}

export interface UserRequestDto {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: string;
  currentPassword?: string;
  newPassword?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  getById(id: number): Observable<ApiResponse<UserResponseDto>> {
    return this.http.get<ApiResponse<UserResponseDto>>(API_URLS.GET_USER(id));
  }

  update(
    id: number,
    payload: UserRequestDto,
  ): Observable<ApiResponse<UserResponseDto>> {
    return this.http.post<ApiResponse<UserResponseDto>>(
      API_URLS.UPDATE_USER(id),
      payload,
    );
  }

  createAdmin(dto: UserRequestDto): Observable<ApiResponse<UserResponseDto>> {
    return this.http.post<ApiResponse<UserResponseDto>>(
      API_URLS.CREATE_ADMIN,
      dto,
    );
  }
  getAll(
    page = 0,
    size = 20,
  ): Observable<ApiResponse<PageResponse<UserResponseDto>>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http.get<ApiResponse<PageResponse<UserResponseDto>>>(
      API_URLS.GET_USERS,
      { params },
    );
  }
  delete(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(API_URLS.DELETE_USER(id));
  }
}
