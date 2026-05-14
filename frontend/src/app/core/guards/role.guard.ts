import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRole: string = route.data['expectedRole'];
  const userRole = authService.getRole();

  if (!userRole) {
    authService.logout();
    return router.parseUrl('/login');
  }

  if (userRole === expectedRole) {
    return true;
  }

  authService.logout();
  return router.parseUrl('/login');
};
