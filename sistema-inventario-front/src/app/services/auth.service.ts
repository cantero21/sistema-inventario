import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

// Interfaz para la respuesta del login
export interface LoginResponse {
  token: string;
  nombre: string;
  email: string;
  rol: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  // Envía credenciales y guarda el token en localStorage
  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(res => {
          localStorage.setItem('token', res.token);
          localStorage.setItem('nombre', res.nombre);
          localStorage.setItem('email', res.email);
          localStorage.setItem('rol', res.rol);
        })
      );
  }

  register(nombre: string, email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, { nombre, email, password, rol: 'USER' });
  }

  logout(): void {
    localStorage.clear();
  }

  // Verifica si hay un token guardado
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getNombre(): string {
    return localStorage.getItem('nombre') || '';
  }

  getRol(): string {
    return localStorage.getItem('rol') || '';
  }
}
