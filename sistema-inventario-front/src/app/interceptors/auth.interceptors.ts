import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  const router = inject(Router);

  const cloned = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(cloned).pipe(
    catchError(error => {
      if (error.status === 401) {
        // Token expirado → logout
        localStorage.clear();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
