import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthStore } from '../services/auth-store';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);
  const accessToken = authStore.getAccessToken();
  const isAuthenticationRequest =
    request.url.includes('/auth/login') || request.url.includes('/auth/register');

  const authenticatedRequest = accessToken
    ? request.clone({
        setHeaders: {
          Authorization: `Bearer ${accessToken}`,
        },
      })
    : request;

  return next(authenticatedRequest).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && !isAuthenticationRequest) {
        authStore.clearSession();
        void router.navigate(['/login'], {
          queryParams: {
            reason: 'expired',
          },
        });
      }

      if (error instanceof HttpErrorResponse && error.status === 403) {
        void router.navigateByUrl(authStore.homeUrl());
      }

      return throwError(() => error);
    }),
  );
};
