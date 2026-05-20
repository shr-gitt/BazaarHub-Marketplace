import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthModelService {
  private showModalSubject = new BehaviorSubject<boolean>(false);
  private modeSubject = new BehaviorSubject<'LOGIN' | 'REGISTER'>('LOGIN');

  showModal$ = this.showModalSubject.asObservable();
  mode$ = this.modeSubject.asObservable();

  open(mode: 'LOGIN' | 'REGISTER' = 'LOGIN') {
    this.modeSubject.next(mode);
    this.showModalSubject.next(true);
    document.body.style.overflow = 'hidden';
  }

  close() {
    this.showModalSubject.next(false);
    document.body.style.overflow = 'auto';
  }
}
