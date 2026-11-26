import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const esAuth = req.url.includes('/auth/');

      if (error.status === 401 && !esAuth) {
        auth.logout();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        alert('No tienes permisos para esta acción');
      } else if (error.status === 500) {
        alert('Error del servidor');
      }

      return throwError(() => error);
    })
  );
};
