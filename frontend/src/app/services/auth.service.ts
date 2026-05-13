import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from '../core/models/auth.model';
import { ApiResponse } from '../core/models/api-response.model';
import { AuthStateService } from './auth-state.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private authState: AuthStateService,
  ) {
    this.loadFromStorage();
  }

  // -------------------------
  // LOGIN
  // -------------------------
  login(data: LoginRequest) {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, data)
      .pipe(
        tap((res) => {
          this.setSession(res?.data);
        }),
      );
  }

  // -------------------------
  // REGISTER
  // -------------------------
  register(data: RegisterRequest) {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, data)
      .pipe(
        tap((res) => {
          this.setSession(res?.data);
        }),
      );
  }

  // -------------------------
  // SESSION SET
  // -------------------------
  private setSession(data?: AuthResponse): void {
    if (!data) return;

    const authData = {
      token: data.token,
      role: data.role,
      userId: data.userId,
    };

    this.authState.setState(authData);

    if (typeof window !== 'undefined') {
      localStorage.setItem('token', data.token);
      localStorage.setItem('role', data.role);
      localStorage.setItem('userId', String(data.userId));
    }
  }

  // -------------------------
  // LOAD FROM STORAGE (on refresh)
  // -------------------------
  private loadFromStorage(): void {
    if (typeof window === 'undefined') return;

    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const userId = localStorage.getItem('userId');

    if (token) {
      this.authState.setState({
        token,
        role,
        userId: userId ? Number(userId) : null,
      });
    }
  }

  // -------------------------
  // LOGOUT
  // -------------------------
  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.clear();
    }
    this.authState.clear();
  }

  // -------------------------
  // SNAPSHOT GETTERS (NO LOCALSTORAGE ANYMORE)
  // -------------------------
  getToken(): string | null {
    return this.authState.getSnapshot().token;
  }

  getRole(): string | null {
    return this.authState.getSnapshot().role;
  }

  getUserId(): number | null {
    return this.authState.getSnapshot().userId;
  }

  // -------------------------
  // ROLE HELPERS
  // -------------------------
  isLoggedIn(): boolean {
    return !!this.getToken();
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
