
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Interfaces de los modelos (lo que devuelve el backend)
export interface Categoria {
  id?: number;
  nombre: string;
  descripcion: string;
}

export interface Proveedor {
  id?: number;
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
}

export interface Cliente {
  id?: number;
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
}

export interface Producto {
  id?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  categoria?: Categoria;
  proveedor?: Proveedor;
}

export interface Usuario {
  id?: number;
  nombre: string;
  email: string;
  rol: string;
}

export interface FacturaItem {
  id?: number;
  cantidad: number;
  precioUnitario: number;
  producto?: Producto;
}

export interface Factura {
  id?: number;
  fecha: string;
  cliente?: Cliente;
  usuario?: Usuario;
  total: number;
  items: FacturaItem[];
}

export interface Movimiento {
  id?: number;
  fecha: string;
  tipo: 'ENTRADA' | 'SALIDA';
  cantidad: number;
  precioUnitario: number;
  producto?: Producto;
}

export interface DashboardTotales {
  producto: number;
  cliente: number;
  proveedor: number;
  factura: number;
}

// Servicio genérico que sirve para cualquier entidad CRUD
@Injectable({ providedIn: 'root' })
export class ApiService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // === DASHBOARD ===
  getDashboardTotales(): Observable<DashboardTotales> {
    return this.http.get<DashboardTotales>(`${this.apiUrl}/dashboard/totales`);
  }

  // === MÉTODOS GENÉRICOS CRUD ===
  // Se reutilizan para categorías, productos, clientes, etc.
  getAll<T>(endpoint: string): Observable<T[]> {
    return this.http.get<T[]>(`${this.apiUrl}/${endpoint}`);
  }

  getById<T>(endpoint: string, id: number): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}/${endpoint}/${id}`);
  }

  create<T>(endpoint: string, data: T): Observable<T> {
    return this.http.post<T>(`${this.apiUrl}/${endpoint}`, data);
  }

  update<T>(endpoint: string, id: number, data: T): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}/${endpoint}/${id}`, data);
  }

  delete(endpoint: string, id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${endpoint}/${id}`);
  }

  // === MOVIMIENTOS (endpoints especiales) ===
  crearEntrada(productoId: number, cantidad: number, precioUnitario: number): Observable<Movimiento> {
    return this.http.post<Movimiento>(`${this.apiUrl}/movimientos/entrada`, {
      producto: { id: productoId },
      cantidad,
      precioUnitario
    });
  }

  crearSalida(productoId: number, cantidad: number, precioUnitario: number): Observable<Movimiento> {
    return this.http.post<Movimiento>(`${this.apiUrl}/movimientos/salida`, {
      producto: { id: productoId },
      cantidad,
      precioUnitario
    });
  }

  getMovimientosPorProducto(productoId: number): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.apiUrl}/movimientos/producto/${productoId}`);
  }

  // === FACTURAS (endpoints especiales) ===
  anularFactura(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/facturas/anular/${id}`, {});
  }
}
