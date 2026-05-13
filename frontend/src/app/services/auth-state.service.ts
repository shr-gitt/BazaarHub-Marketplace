import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface AuthState {
  token: string | null;
  role: string | null;
  userId: number | null;
}

@Injectable({ providedIn: 'root' })
export class AuthStateService {
  private stateSubject = new BehaviorSubject<AuthState>({
    token: null,
    role: null,
    userId: null,
  });

  state$ = this.stateSubject.asObservable();

  setState(state: AuthState): void {
    this.stateSubject.next(state);
  }

  update(partial: Partial<AuthState>): void {
    this.stateSubject.next({
      ...this.stateSubject.value,
      ...partial,
    });
  }

  getSnapshot(): AuthState {
    return this.stateSubject.value;
  }

  clear(): void {
    this.stateSubject.next({
      token: null,
      role: null,
      userId: null,
    });
  }
}