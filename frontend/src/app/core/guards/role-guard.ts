import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { UserRole } from '../models/auth.models';
import { AuthStore } from '../services/auth-store';

const resolveExpectedRoles = (route: ActivatedRouteSnapshot): UserRole[] =>
  (route.data['roles'] as UserRole[] | undefined) ?? [];

export const roleGuard: CanActivateFn = (route) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);
  const currentRole = authStore.role();
  const expectedRoles = resolveExpectedRoles(route);

  if (currentRole && expectedRoles.includes(currentRole)) {
    return true;
  }

  return router.createUrlTree([authStore.homeUrl()]);
};
