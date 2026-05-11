import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  // Role-based guard: route data.roles = ['ADMIN', 'VENDOR'] etc.
  const requiredRoles: string[] | undefined = route.data?.['roles'];
  if (requiredRoles && requiredRoles.length > 0) {
    const userRole = auth.getRole();
    if (!userRole || !requiredRoles.includes(userRole)) {
      // Redirect to appropriate dashboard
      const role = auth.getRole();
      if (role === 'ADMIN') router.navigate(['/admin/dashboard']);
      else if (role === 'VENDOR') router.navigate(['/vendor/dashboard']);
      else router.navigate(['/customer/dashboard']);
      return false;
    }
  }

  return true;
};
