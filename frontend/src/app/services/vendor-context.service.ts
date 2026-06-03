import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class VendorContextService {
  private isBrowser: boolean;

  private vendorIdSubject = new BehaviorSubject<number | null>(null);
  vendorId$ = this.vendorIdSubject.asObservable();

  constructor(@Inject(PLATFORM_ID) private platformId: object) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    this.vendorIdSubject.next(this.getStoredVendorId());
  }

  setVendorId(id: number): void {
    localStorage.setItem('vendorId', id.toString());
    this.vendorIdSubject.next(id);
  }

  getVendorId(): number | null {
    return this.vendorIdSubject.value;
  }

  clear(): void {
    localStorage.removeItem('vendorId');
    this.vendorIdSubject.next(null);
  }

  private getStoredVendorId(): number | null {
    if (!this.isBrowser) return null;

    const value = localStorage.getItem('vendorId');
    return value ? Number(value) : null;
  }
}
