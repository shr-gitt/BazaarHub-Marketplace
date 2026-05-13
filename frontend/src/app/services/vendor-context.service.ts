import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class VendorContextService {
  private vendorIdSubject = new BehaviorSubject<number | null>(null);

  vendorId$ = this.vendorIdSubject.asObservable();

  setVendorId(id: number): void {
    this.vendorIdSubject.next(id);
  }

  getVendorId(): number | null {
    return this.vendorIdSubject.value;
  }

  clear(): void {
    this.vendorIdSubject.next(null);
  }
}