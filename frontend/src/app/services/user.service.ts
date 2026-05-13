import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../core/models/api-response.model';

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
      private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<ApiResponse<UserResponseDto>> {
    return this.http.get<ApiResponse<UserResponseDto>>(`${this.base}/user/${id}`);
  }

  update(id: number, payload: UserRequestDto): Observable<ApiResponse<UserResponseDto>> {
    return this.http.post<ApiResponse<UserResponseDto>>(`${this.base}/update-user/${id}`, payload);
  }
}