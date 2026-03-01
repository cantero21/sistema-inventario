import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent) },

  // Rutas protegidas (requieren login)
  { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [authGuard] },
  { path: 'productos', loadComponent: () => import('./components/productos/productos.component').then(m => m.ProductosComponent), canActivate: [authGuard] },
  { path: 'categorias', loadComponent: () => import('./components/categorias/categorias.component').then(m => m.CategoriasComponent), canActivate: [authGuard] },
  { path: 'clientes', loadComponent: () => import('./components/clientes/clientes.component').then(m => m.ClientesComponent), canActivate: [authGuard] },
  { path: 'proveedores', loadComponent: () => import('./components/proveedores/proveedores.component').then(m => m.ProveedoresComponent), canActivate: [authGuard] },
  { path: 'facturas', loadComponent: () => import('./components/facturas/facturas.component').then(m => m.FacturasComponent), canActivate: [authGuard] },
  { path: 'facturas/nueva', loadComponent: () => import('./components/factura-form/factura-form.component').then(m => m.FacturaFormComponent), canActivate: [authGuard] },
  { path: 'movimientos', loadComponent: () => import('./components/movimientos/movimientos.component').then(m => m.MovimientosComponent), canActivate: [authGuard] },
  { path: 'usuarios', loadComponent: () => import('./components/usuarios/usuarios.component').then(m => m.UsuariosComponent), canActivate: [authGuard] },

  // Ruta por defecto
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
