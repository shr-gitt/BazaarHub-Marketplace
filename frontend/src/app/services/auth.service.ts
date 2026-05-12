import { HttpClient } from '@angular/common/http';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from '../core/models/auth.model';
import { ApiResponse } from '../core/models/api-response.model';
import { Observable, tap } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }
  private baseUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}
  login(data: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, data)
      .pipe(
        tap((res) => {
          if (this.isBrowser() && res?.data?.token) {
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('role', res.data.role);
            localStorage.setItem('userId', String(res.data.userId));
          }
        }),
      );
  }

  register(data: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, data)
      .pipe(
        tap((res) => {
          if (this.isBrowser() && res?.data?.token) {
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('role', res.data.role);
            localStorage.setItem('userId', String(res.data.userId));
          }
        }),
      );
  }

  logout(): void {
    if (this.isBrowser()) {
      localStorage.clear();
    }
  }
  getToken(): string | null {
    if (!this.isBrowser()) return null;
    return localStorage.getItem('token');
  }
  getRole(): string | null {
    if (!this.isBrowser()) return null;
    return localStorage.getItem('role');
  }
  getUserId(): number | null {
    if (!this.isBrowser()) return null;
    const id = localStorage.getItem('userId');
    return id ? Number(id) : null;
  }
  getMessage(): string | null {
    if (!this.isBrowser()) return null;
    return localStorage.getItem('message');
  }
  isLoggedIn(): boolean {
    if (!this.isBrowser()) return false;
    return !!localStorage.getItem('token');
  }
  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
  isVendor(): boolean {
    return this.getRole() === 'VENDOR';
  }
  isCustomer(): boolean {
    return this.getRole() === 'CUSTOMER';
  }
}
