import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../core/models/api-response.model';
import { API_URLS } from '../core/constants/api-urls';
import { NotificationResponse } from '../core/models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<ApiResponse<NotificationResponse[]>> {
    return this.http.get<ApiResponse<NotificationResponse[]>>(
      API_URLS.GET_NOTIFICATIONS,
    );
  }

  getUnreadCount(): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(API_URLS.GET_UNREAD_COUNT);
  }

  markAsRead(id: number): Observable<ApiResponse<NotificationResponse>> {
    return this.http.patch<ApiResponse<NotificationResponse>>(
      API_URLS.MARK_AS_READ(id),
      {},
    );
  }

  markAllAsRead(): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(API_URLS.MARK_ALL_READ, {});
  }
}
