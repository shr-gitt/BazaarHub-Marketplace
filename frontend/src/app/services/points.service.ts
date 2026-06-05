import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URLS } from '../core/constants/api-urls';

@Injectable({ providedIn: 'root' })
export class PointsService {

  constructor(private http: HttpClient) {}

  getMyPoints(): Observable<any> {
    return this.http.get<any>(API_URLS.MY_POINTS);
  }
}